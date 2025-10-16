package com.atelierlocal.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConversationSummaryDTO {
    private UUID otherUserId;
    private String otherUserName;
    private String otherUserRole;
    private String lastMessage;
    @JsonProperty("timestamp")
    private LocalDateTime createdAt;

    public ConversationSummaryDTO(UUID otherUserId, String otherUserName, String otherUserRole, String lastMessage, LocalDateTime createdAt) {
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;
        this.otherUserRole = otherUserRole;
        this.lastMessage = lastMessage;
        this.createdAt = createdAt;
    }

    public UUID getOtherUserId() { return otherUserId; }
    public String getOtherUserName() { return otherUserName; }
    public String getOtherUserRole() { return otherUserRole; }
    public String getLastMessage() { return lastMessage; }
    public LocalDateTime getLastTimestamp() { return createdAt; }
}
