package com.atelierlocal.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.atelierlocal.model.Attachment;
import com.atelierlocal.model.Message;
import com.atelierlocal.model.MessageStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageResponseDTO {
    private UUID id;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private String messageError;
    private MessageStatus messageStatus;
    private List<AttachmentDTO> attachments; 
    @JsonProperty("timestamp")
    private LocalDateTime createdAt;
    private String tempId;

    // Constructeur pour un message
    public MessageResponseDTO(Message message) {
        this.id = message.getId();
        this.senderId = message.getSender().getId();
        this.receiverId = message.getReceiver().getId();
        this.content = message.getContent();
        this.messageStatus = message.getMessageStatus();
        this.messageError = message.getMessageError();
        this.createdAt = message.getCreatedAt();
        this.tempId = message.getTempId();
        // Mapping des attachments vers AttachmentDTO
        this.attachments = message.getAttachments() != null
            ? message.getAttachments().stream()
                .map(AttachmentDTO::new)
                .collect(Collectors.toList())
            : null;
    }

    // Constructeur pour un message d'erreur
    public MessageResponseDTO(String errorMessage) {
        this.messageError = errorMessage;
        this.messageStatus = MessageStatus.FAILED;
    }

    // Classe interne pour représenter les pièces jointes
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

    // Getters et setters
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

    public String getTempId() { return tempId; }
    public void setTempId(String tempId) { this.tempId = tempId; }
}