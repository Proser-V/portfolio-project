package com.atelierlocal.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.atelierlocal.model.Attachment;
import com.atelierlocal.model.Message;
import com.atelierlocal.model.MessageStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de réponse pour représenter un message échangé entre deux utilisateurs.
 * 
 * Ce DTO contient :
 * - Les informations sur l'expéditeur et le destinataire (senderId, receiverId)
 * - Le contenu textuel du message
 * - L'état du message (MessageStatus)
 * - Les éventuelles erreurs liées à l'envoi du message (messageError)
 * - La liste des pièces jointes associées au message (attachments)
 * - La date de création du message
 * - Le statut de lecture du message
 * - L'identifiant temporaire utilisé côté client (tempId)
 * 
 * Il fournit également un constructeur spécifique pour les messages d'erreur.
 */
public class MessageResponseDTO {
    
    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------
    
    private UUID id;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private String messageError;
    private MessageStatus messageStatus;
    private List<AttachmentDTO> attachments; 
    @JsonProperty("timestamp")
    private LocalDateTime createdAt;
    @JsonProperty("isRead")
    private Boolean isRead;
    private String tempId;

    // -------------------------------------------------------------------------
    // CONSTRUCTEURS
    // -------------------------------------------------------------------------

    /**
     * Constructeur principal pour initialiser le DTO à partir d'une entité Message.
     * Effectue également le mapping des attachments vers AttachmentDTO.
     */
    public MessageResponseDTO(Message message) {
        this.id = message.getId();
        this.senderId = message.getSender().getId();
        this.receiverId = message.getReceiver().getId();
        this.content = message.getContent();
        this.messageStatus = message.getMessageStatus();
        this.messageError = message.getMessageError();
        this.createdAt = message.getCreatedAt();
        this.isRead = message.getRead();
        this.tempId = message.getTempId();
        this.attachments = message.getAttachments() != null
            ? message.getAttachments().stream()
                .map(AttachmentDTO::new)
                .collect(Collectors.toList())
            : null;
    }

    /**
     * Constructeur pour créer un DTO représentant un message ayant échoué.
     * Initialise le statut du message à FAILED.
     */
    public MessageResponseDTO(String errorMessage) {
        this.messageError = errorMessage;
        this.messageStatus = MessageStatus.FAILED;
    }

    // -------------------------------------------------------------------------
    // CLASSE INTERNE POUR LES PIECES JOINTES
    // -------------------------------------------------------------------------

    /**
     * DTO pour représenter une pièce jointe associée à un message.
     */
    public static class AttachmentDTO {
        private UUID id;
        private String fileUrl;
        private String fileType;

        public AttachmentDTO(Attachment attachment) {
            this.id = attachment.getId();
            this.fileUrl = attachment.getFileUrl();
            this.fileType = attachment.getFileType();
        }

        // Getters et setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getFileUrl() { return fileUrl; }
        public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

        public String getFileType() { return fileType; }
        public void setFileType(String fileType) { this.fileType = fileType; }
    }

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getSenderId() { return senderId; }
    public void setSenderId(UUID senderId) { this.senderId = senderId; }

    public UUID getReceiverId() { return receiverId; }
    public void setReceiverId(UUID receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMessageError() { return messageError; }
    public void setMessageError(String messageError) { this.messageError = messageError; }

    public MessageStatus getMessageStatus() { return messageStatus; }
    public void setMessageStatus(MessageStatus messageStatus) { this.messageStatus = messageStatus; }

    public List<AttachmentDTO> getAttachments() { return attachments; }
    public void setAttachments(List<AttachmentDTO> attachments) { this.attachments = attachments; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @JsonProperty("isRead")
    public Boolean getRead() { return isRead; }
    public void setRead(Boolean isRead) { this.isRead = isRead; }

    public String getTempId() { return tempId; }
    public void setTempId(String tempId) { this.tempId = tempId; }
}
