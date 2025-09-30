package com.atelierlocal.dto;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;

public class MessageRequestDTO {
    // Attributs
    @NotBlank(message = "L'ID de l'envoyeur est requise")
    private UUID senderId;

    @NotBlank(message = "L'ID du receveur est requise")
    private UUID receiverId;

    private String content;

    private MultipartFile file;

    // Getters + setters
    public UUID getSenderId() { return senderId; }
    public void setSenderId(UUID senderId) { this.senderId = senderId; }

    public UUID getReceiverId() { return receiverId; }
    public void setReceiverId(UUID receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }
}
