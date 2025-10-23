package com.atelierlocal.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.atelierlocal.dto.ConversationSummaryDTO;
import com.atelierlocal.dto.MessageRequestDTO;
import com.atelierlocal.dto.MessageResponseDTO;
import com.atelierlocal.model.Message;
import com.atelierlocal.model.User;
import com.atelierlocal.model.UserRole;
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "API pour la messagerie")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final ArtisanRepo artisanRepo;
    private final ClientRepo clientRepo;

    public MessageController(SimpMessagingTemplate messagingTemplate, MessageService messageService, 
                             ArtisanRepo artisanRepo, ClientRepo clientRepo) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.artisanRepo = artisanRepo;
        this.clientRepo = clientRepo;
    }

    // Vérifie si l’envoi est autorisé selon les rôles
    private void checkMessageAuthorization(User sender, User receiver) {
        UserRole senderRole = sender.getUserRole();
        UserRole receiverRole = receiver.getUserRole();

        if (senderRole == UserRole.ARTISAN) {
            if (!(receiverRole == UserRole.ADMIN || receiverRole == UserRole.CLIENT)) {
                throw new IllegalArgumentException("Les artisans ne peuvent contacter que des clients ou des administrateurs.");
            }
        }
        else if (senderRole == UserRole.CLIENT) {
            if (!(receiverRole == UserRole.ADMIN || receiverRole == UserRole.ARTISAN)) {
                throw new IllegalArgumentException("Les clients ne peuvent contacter que des artisans ou des administrateurs.");
            }
        }
    }

    // Récupérer l'utilisateur authentifié
    private User getAuthenticatedUser(Principal principal) {
        String email = principal.getName();
        return artisanRepo.findByEmail(email)
            .map(User.class::cast)
            .orElseGet(() -> clientRepo.findByEmail(email)
                .map(User.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé: " + email)));
    }

    // Endpoint REST pour messages avec fichiers
    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'ARTISAN')")
    @Operation(summary = "Envoie un message avec pièce jointe optionnelle")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message envoyé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<MessageResponseDTO> sendMessageWithAttachment(
            @RequestParam("receiverId") UUID receiverId,
            @RequestParam(value = "content", required = false, defaultValue = "") String content,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Principal principal
    ) {
        try {
            // Récupérer l’utilisateur authentifié
            User authenticatedUser = getAuthenticatedUser(principal);

            // Récupérer le destinataire
            User receiverUser = artisanRepo.findById(receiverId)
                .map(User.class::cast)
                .orElseGet(() -> clientRepo.findById(receiverId)
                    .map(User.class::cast)
                    .orElseThrow(() -> new IllegalArgumentException("Destinataire non trouvé: " + receiverId)));

            // Vérifier les autorisations
            checkMessageAuthorization(authenticatedUser, receiverUser);

            // Créer le DTO
            MessageRequestDTO dto = new MessageRequestDTO();
            dto.setSenderId(authenticatedUser.getId());
            dto.setReceiverId(receiverId);
            dto.setContent(content);
            dto.setFile(file);

            logger.info("Envoi message REST de {} à {} avec fichier: {}", 
                authenticatedUser.getId(), receiverId, file != null ? file.getOriginalFilename() : "aucun");

            // Envoyer via le service
            MessageResponseDTO response = messageService.sendMessage(dto);

            // Diffuser via WebSocket aux deux parties
            messagingTemplate.convertAndSendToUser(
                receiverUser.getEmail(),
                "/queue/messages",
                response
            );

            messagingTemplate.convertAndSendToUser(
                authenticatedUser.getEmail(),
                "/queue/messages",
                response
            );

            logger.info("Message envoyé avec succès");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Erreur validation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                new MessageResponseDTO("Erreur: " + e.getMessage())
            );
        } catch (Exception e) {
            logger.error("Erreur inattendue: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new MessageResponseDTO("Erreur serveur: " + e.getMessage())
            );
        }
    }

    // WebSocket pour messages SANS fichiers (texte uniquement)
    @MessageMapping("/chat")
    public void processMessage(@Valid MessageRequestDTO message, Principal principal) {
        try {
            logger.info("Message reçu via WebSocket de: {}", principal.getName());
            
            User authenticatedUser = getAuthenticatedUser(principal);
            UUID authenticatedId = authenticatedUser.getId();
            
            // Récupérer le destinataire
            User receiverUser = artisanRepo.findById(message.getReceiverId())
                .map(User.class::cast)
                .orElseGet(() -> clientRepo.findById(message.getReceiverId())
                    .map(User.class::cast)
                    .orElseThrow(() -> new IllegalArgumentException("Destinataire non trouvé: " + message.getReceiverId())));

            // Vérifier les autorisations
            checkMessageAuthorization(authenticatedUser, receiverUser);

            message.setSenderId(authenticatedId);
            message.setFile(null); // Pas de fichier via WebSocket

            logger.info("Traitement message de {} à {}", message.getSenderId(), message.getReceiverId());
            
            MessageResponseDTO response = messageService.sendMessage(message);

            messagingTemplate.convertAndSendToUser(
                receiverUser.getEmail(),
                "/queue/messages",
                response
            );

            messagingTemplate.convertAndSendToUser(
                authenticatedUser.getEmail(),
                "/queue/messages",
                response
            );
            
            logger.info("Message envoyé à {} et {}", receiverUser.getEmail(), authenticatedUser.getEmail());

        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors du traitement du message : {}", e.getMessage());
            MessageResponseDTO errorResponse = new MessageResponseDTO(
                "Erreur lors de l'envoi du message : " + e.getMessage()
            );
            if (principal != null) {
                messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/messages",
                    errorResponse
                );
            }
        } catch (Exception e) {
            logger.error("Erreur inattendue: {}", e.getMessage(), e);
            MessageResponseDTO errorResponse = new MessageResponseDTO(
                "Erreur serveur : " + e.getMessage()
            );
            if (principal != null) {
                messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/messages",
                    errorResponse
                );
            }
        }
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'ARTISAN')")
    @Operation(summary = "Récupère l'historique de conversation entre deux utilisateurs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historique récupéré avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "400", description = "Paramètres invalides")
    })
    public ResponseEntity<List<MessageResponseDTO>> getHistory(
        @RequestParam UUID user1Id,
        @RequestParam UUID user2Id,
        Principal principal
    ) {
        User authenticatedUser = getAuthenticatedUser(principal);
        UUID authId = authenticatedUser.getId();

        if (!(authId.equals(user1Id) || authId.equals(user2Id))) {
            logger.warn("⚠️ Accès refusé: authId={} n'est ni user1Id={} ni user2Id={}", authId, user1Id, user2Id);
            return ResponseEntity.status(403).build();
        }

        try {
            List<MessageResponseDTO> conversation = messageService.getConversation(user1Id, user2Id);
            logger.info("Historique récupéré: {} messages", conversation.size());
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'historique: {}", e.getMessage(), e);
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/conversations/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'ARTISAN')")
    @Operation(summary = "Récupère les conversations d'un utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conversations récupérées avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "404", description = "Aucune conversation trouvée")
    })
    public ResponseEntity<List<ConversationSummaryDTO>> getConversations(
        @PathVariable UUID userId,
        Principal principal
    ) {
        logger.info("Principal reçu: {}", principal != null ? principal.getName() : "null");
        User authenticatedUser = getAuthenticatedUser(principal);
        UUID authId = authenticatedUser.getId();
        logger.info("Utilisateur authentifié: id={}, email={}", authId, authenticatedUser.getEmail());

        if (!authId.equals(userId)) {
            logger.warn("⚠️ Tentative d'accès aux conversations d'un autre utilisateur = auth={}, request={}", authId, userId);
            return ResponseEntity.status(403).build();
        }

        try {
            List<ConversationSummaryDTO> conversations = messageService.getConversationSummaries(userId);
            logger.info("Conversations récupérées: {}", conversations.size());
            return ResponseEntity.ok(conversations);
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la récupération des conversations : {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erreur inattendue : {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/unread")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'ARTISAN')")
    @Operation(summary = "Récupère les messages non lus de l'utilisateur authentifié")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Messages non lus récupérés avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "404", description = "Aucun message non lu trouvé")
    })
    public ResponseEntity<List<Message>> getUnreadMessages(Principal principal) {
        try {
            User authenticatedUser = getAuthenticatedUser(principal);
            List <Message> unreadMessages = messageService.getUnreadMessages(authenticatedUser);

            return ResponseEntity.ok(unreadMessages);
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la récupération des messages non lus : {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erreur inattendue : {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{messageId}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'ARTISAN')")
    @Operation(summary = "Marque un message comme lu")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message marqué comme lu avec succès"),
        @ApiResponse(responseCode = "400", description = "Message non trouvé ou utilisateur non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> markMessageAsRead(@PathVariable UUID messageId, Principal principal) {
        try {
            User authenticatedUser = getAuthenticatedUser(principal);
            messageService.markMessageAsRead(messageId, authenticatedUser);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors du marquage du message comme lu: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erreur inattendue: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}