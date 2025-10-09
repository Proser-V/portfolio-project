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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.MessageRequestDTO;
import com.atelierlocal.dto.MessageResponseDTO;
import com.atelierlocal.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
@Tag(name = "Messages", description = "API pour la messagerie")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    public MessageController(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    @MessageMapping("/chat")
    public void processMessage(@Valid MessageRequestDTO message, Principal principal) {
        try {
            UUID authenticatedId = UUID.fromString(principal.getName());
            message.setSenderId(authenticatedId);

            logger.info("Réception d'un message de {} à {}", message.getSenderId(), message.getReceiverId());
            MessageResponseDTO response = messageService.sendMessage(message);

            messagingTemplate.convertAndSendToUser(
                response.getReceiverId().toString(),
                "/queue/messages",
                response
            );

            logger.info("Message envoyé à {} via WebSocket", response.getReceiverId());
        } catch (Exception e) {
            logger.error("Erreur lors du traitement du message : {}", e.getMessage(), e);

            MessageResponseDTO errorResponse = new MessageResponseDTO(
                "Erreur lors de l'envoi du message : " + e.getMessage()
            );

            String receiver = (message.getReceiverId() != null) ? message.getReceiverId().toString() : null;
            if (receiver != null) {
                messagingTemplate.convertAndSendToUser(
                    receiver,
                    "/queue/messages",
                    errorResponse
                );
            }

            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/messages",
                errorResponse
            );
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
        UUID authId = UUID.fromString(principal.getName());
        if (!(authId.equals(user1Id) || authId.equals(user2Id))) {
            return ResponseEntity.status(403).build();
        }
        List<MessageResponseDTO> conversation = messageService.getConversation(user1Id, user2Id);
        return ResponseEntity.ok(conversation);
    }
}
