package com.atelierlocal.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConversationSummaryDTO {
    private UUID otherUserId;
    private String otherUserName;
    private String otherUserRole;
    private String lastMessage;
    private LocalDateTime lastTimestamp;

    public ConversationSummaryDTO(UUID otherUserId, String otherUserName, String otherUserRole, String lastMessage, LocalDateTime lastTimestamp) {
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;
        this.otherUserRole = otherUserRole;
        this.lastMessage = lastMessage;
        this.lastTimestamp = lastTimestamp;
    }

    public UUID getOtherUserId() { return otherUserId; }
    public String getOtherUserName() { return otherUserName; }
    public String getOtherUserRole() { return otherUserRole; }
    public String getLastMessage() { return lastMessage; }
    public LocalDateTime getLastTimestamp() { return lastTimestamp; }
}
