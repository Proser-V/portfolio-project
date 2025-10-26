package com.atelierlocal.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

/**
 * Contrôleur REST et WebSocket pour la messagerie de l'application.
 * 
 * Ce contrôleur gère :
 * - L'envoi de messages (texte et fichiers)
 * - La réception via WebSocket
 * - La récupération de l'historique de conversation
 * - La gestion des conversations et messages non lus
 * - Le marquage des messages comme lus
 * 
 * Les accès sont sécurisés selon les rôles : ADMIN, CLIENT, ARTISAN.
 */
@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "API pour la messagerie")
public class MessageController {

    // Logger pour le suivi des événements et erreurs
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    // Composants nécessaires au fonctionnement du contrôleur
    private final SimpMessagingTemplate messagingTemplate; // Pour envoyer des messages via WebSocket
    private final MessageService messageService;           // Service métier pour la gestion des messages
    private final ArtisanRepo artisanRepo;                // Répertoire pour accéder aux artisans
    private final ClientRepo clientRepo;                  // Répertoire pour accéder aux clients

    /**
     * Constructeur du contrôleur avec injection des dépendances.
     */
    public MessageController(SimpMessagingTemplate messagingTemplate, MessageService messageService, 
                             ArtisanRepo artisanRepo, ClientRepo clientRepo) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.artisanRepo = artisanRepo;
        this.clientRepo = clientRepo;
    }

    /**
     * Vérifie si l’envoi d’un message est autorisé selon les rôles des utilisateurs.
     * 
     * Règles :
     * - Un artisan ne peut contacter que des clients ou des administrateurs.
     * - Un client ne peut contacter que des artisans ou des administrateurs.
     * 
     * @param sender L'utilisateur qui envoie le message
     * @param receiver L'utilisateur destinataire
     * @throws IllegalArgumentException si l'envoi n'est pas autorisé
     */
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

    /**
     * Récupère l'utilisateur authentifié depuis le Principal fourni par Spring Security.
     * 
     * @param principal Objet représentant l'utilisateur authentifié
     * @return L'objet User correspondant
     * @throws IllegalArgumentException si l'utilisateur n'existe pas
     */
    private User getAuthenticatedUser(Principal principal) {
        String email = principal.getName();
        return artisanRepo.findByEmail(email)
            .map(User.class::cast)
            .orElseGet(() -> clientRepo.findByEmail(email)
                .map(User.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé: " + email)));
    }

    // -------------------------------------------------------------------------
    // ENVOI DE MESSAGES AVEC FICHIERS (REST)
    // -------------------------------------------------------------------------
    
    /**
     * Endpoint REST pour envoyer un message avec une pièce jointe optionnelle.
     * 
     * @param receiverId UUID du destinataire
     * @param content Contenu textuel du message (optionnel)
     * @param file Fichier joint (optionnel)
     * @param principal Utilisateur authentifié
     * @return MessageResponseDTO contenant les informations du message envoyé
     */
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
            // Récupération de l'utilisateur authentifié
            User authenticatedUser = getAuthenticatedUser(principal);

            // Récupération du destinataire
            User receiverUser = artisanRepo.findById(receiverId)
                .map(User.class::cast)
                .orElseGet(() -> clientRepo.findById(receiverId)
                    .map(User.class::cast)
                    .orElseThrow(() -> new IllegalArgumentException("Destinataire non trouvé: " + receiverId)));

            // Vérification des autorisations d'envoi
            checkMessageAuthorization(authenticatedUser, receiverUser);

            // Création du DTO pour le service
            MessageRequestDTO dto = new MessageRequestDTO();
            dto.setSenderId(authenticatedUser.getId());
            dto.setReceiverId(receiverId);
            dto.setContent(content);
            dto.setFile(file);

            logger.info("Envoi message REST de {} à {} avec fichier: {}", 
                authenticatedUser.getId(), receiverId, file != null ? file.getOriginalFilename() : "aucun");

            // Envoi du message via le service
            MessageResponseDTO response = messageService.sendMessage(dto);

            // Diffusion du message via WebSocket aux deux parties
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

    // -------------------------------------------------------------------------
    // ENVOI DE MESSAGES TEXTE (WebSocket)
    // -------------------------------------------------------------------------
    
    /**
     * Traitement des messages texte reçus via WebSocket.
     * 
     * Ce endpoint ne gère pas les fichiers (uniquement le texte).
     * Les messages sont envoyés aux deux utilisateurs via WebSocket.
     * 
     * @param message DTO contenant le message à envoyer
     * @param principal Utilisateur authentifié
     */
    @MessageMapping("/chat")
    public void processMessage(@Valid MessageRequestDTO message, Principal principal) {
        try {
            logger.info("Message reçu via WebSocket de: {}", principal.getName());
            
            // Récupération de l'utilisateur authentifié
            User authenticatedUser = getAuthenticatedUser(principal);
            UUID authenticatedId = authenticatedUser.getId();
            
            // Récupération du destinataire
            User receiverUser = artisanRepo.findById(message.getReceiverId())
                .map(User.class::cast)
                .orElseGet(() -> clientRepo.findById(message.getReceiverId())
                    .map(User.class::cast)
                    .orElseThrow(() -> new IllegalArgumentException("Destinataire non trouvé: " + message.getReceiverId())));

            // Vérification des autorisations
            checkMessageAuthorization(authenticatedUser, receiverUser);

            // Préparation du message pour le service
            message.setSenderId(authenticatedId);
            message.setFile(null); // Pas de fichier via WebSocket

            logger.info("Traitement message de {} à {}", message.getSenderId(), message.getReceiverId());
            
            // Envoi du message via le service
            MessageResponseDTO response = messageService.sendMessage(message);

            // Diffusion WebSocket aux deux utilisateurs
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

    // -------------------------------------------------------------------------
    // HISTORIQUE DE CONVERSATION
    // -------------------------------------------------------------------------
    
    /**
     * Récupère l'historique de conversation entre deux utilisateurs.
     * 
     * Seul un des deux utilisateurs peut accéder à l'historique.
     * 
     * @param user1Id UUID du premier utilisateur
     * @param user2Id UUID du second utilisateur
     * @param principal Utilisateur authentifié
     * @return Liste des messages de la conversation
     */
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

        // Vérification que l'utilisateur authentifié est impliqué dans la conversation
        if (!(authId.equals(user1Id) || authId.equals(user2Id))) {
            logger.warn("Accès refusé: authId={} n'est ni user1Id={} ni user2Id={}", authId, user1Id, user2Id);
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

    // -------------------------------------------------------------------------
    // LISTE DES CONVERSATIONS D'UN UTILISATEUR
    // -------------------------------------------------------------------------
    
    /**
     * Récupère la liste des conversations d'un utilisateur.
     * 
     * @param userId UUID de l'utilisateur
     * @param principal Utilisateur authentifié
     * @return Liste résumée des conversations
     */
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

        // Vérification que l'utilisateur demande ses propres conversations
        if (!authId.equals(userId)) {
            logger.warn("Tentative d'accès aux conversations d'un autre utilisateur = auth={}, request={}", authId, userId);
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

    // -------------------------------------------------------------------------
    // MESSAGES NON LUS
    // -------------------------------------------------------------------------
    
    /**
     * Récupère les messages non lus de l'utilisateur authentifié.
     * 
     * @param principal Utilisateur authentifié
     * @return Liste des messages non lus
     */
    @GetMapping("/unread")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'ARTISAN')")
    @Operation(summary = "Récupère les messages non lus de l'utilisateur authentifié")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Messages non lus récupérés avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "404", description = "Aucun message non lu trouvé")
    })
    public ResponseEntity<List<MessageResponseDTO>> getUnreadMessages(Principal principal) {
        try {
            User authenticatedUser = getAuthenticatedUser(principal);
            List<Message> unreadMessages = messageService.getUnreadMessages(authenticatedUser);
            List<MessageResponseDTO> messageDTOs = unreadMessages.stream()
                .map(MessageResponseDTO::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(messageDTOs);
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la récupération des messages non lus : {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erreur inattendue : {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // -------------------------------------------------------------------------
    // MARQUAGE D'UN MESSAGE COMME LU
    // -------------------------------------------------------------------------
    
    /**
     * Marque un message comme lu et notifie l'utilisateur via WebSocket du nombre de messages non lus restants.
     * 
     * @param messageId UUID du message
     * @param principal Utilisateur authentifié
     */
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

            // Envoi du compteur de messages non lus via WebSocket
            int unreadCount = messageService.getUnreadMessages(authenticatedUser).size();
            messagingTemplate.convertAndSendToUser(
                authenticatedUser.getEmail(),
                "/queue/unread",
                unreadCount
            );
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
