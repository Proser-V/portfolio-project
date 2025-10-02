package com.atelierlocal.controller;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

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
            response);
            logger.info("Message envoyé à {} via WebSocket", response.getReceiverId());
        } catch (Exception e) {
            logger.error("Erreur lors du traitement du message : {}", e.getMessage(), e);
            MessageResponseDTO errorResponse = new MessageResponseDTO(null);
            errorResponse.setMessageError("Erreur lors de l'envoi du message : " + e.getMessage());

            messagingTemplate.convertAndSendToUser(
                message.getReceiverId().toString(),
                "/queue/messages",
                errorResponse);

            messagingTemplate.convertAndSendToUser(
                message.getSenderId().toString(),
                "/queue/messages",
                errorResponse);
        }
    }
}
