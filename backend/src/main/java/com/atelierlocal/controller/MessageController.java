package com.atelierlocal.controller;

import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.atelierlocal.dto.MessageRequestDTO;
import com.atelierlocal.dto.MessageResponseDTO;
import com.atelierlocal.service.MessageService;

import jakarta.validation.Valid;

@Controller
@Validated
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    public MessageController(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    @MessageMapping("/chat")
    public void processMessage(@Valid MessageRequestDTO message) {
        try {
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

            // Utilisation du nouveau constructeur pour les erreurs
            MessageResponseDTO errorResponse = new MessageResponseDTO(
                "Erreur lors de l'envoi du message : " + e.getMessage()
            );

            // On notifie à la fois l'expéditeur et le destinataire
            messagingTemplate.convertAndSendToUser(
                message.getReceiverId().toString(),
                "/queue/messages",
                errorResponse
            );

            messagingTemplate.convertAndSendToUser(
                message.getSenderId().toString(),
                "/queue/messages",
                errorResponse
            );
        }
    }

    @GetMapping("/messages/history")
    public List<MessageResponseDTO> getHistory(
        @RequestParam UUID user1Id,
        @RequestParam UUID user2Id
    ) {
        return messageService.getConversation(user1Id, user2Id);
    }
}
