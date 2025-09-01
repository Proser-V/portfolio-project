package com.atelierlocal.dto;

import java.util.UUID;

public class MessageDTO {
    private UUID senderId;
    private UUID receiverId;
    private String content;

    public MessageDTO() {}

    public MessageDTO(UUID senderId, UUID receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
    }

    // Getters et setters
    public UUID getSenderId() { return senderId; }
    public void setSenderId(UUID senderId) { this.senderId = senderId; }

    public UUID getReceiverId() { return receiverId; }
    public void setReceiverId(UUID receiverId) { this.receiverId = receiverId; }

    public String getContet() { return content; }
    public void setContent(String content) { this.content = content; }
}
