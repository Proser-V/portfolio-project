package com.atelierlocal.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.atelierlocal.model.Asking;
import com.atelierlocal.model.AskingStatus;

public class AskingResponseDTO {
    private UUID id;
    private UUID clientId;
    private String content;
    private UUID artisanCategoryId;
    private UUID eventCategoryId;
    private LocalDateTime eventDate;
    private String eventLocalisation;
    private AskingStatus status;

    public AskingResponseDTO(Asking asking) {
        this.id = asking.getId();
        this.clientId = asking.getClient().getId();
        this.content = asking.getContent();
        this.artisanCategoryId = asking.getArtisanCategory().getId();
        this.eventCategoryId = asking.getEventCategory() != null ? asking.getEventCategory().getId() : null;
        this.eventDate = asking.getEventDate() != null ? asking.getEventDate() : null;
        this.eventLocalisation = asking.getEventLocalisation() != null ? asking.getEventLocalisation() : null;
        this.status = asking.getStatus();
    }

    public UUID getId() { return id; }

    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public UUID getArtisanCategoryId() { return artisanCategoryId; }
    public void setArtisanCategoryId(UUID artisanCategoryId) { this.artisanCategoryId = artisanCategoryId; }

    public UUID getEventCategoryId() { return eventCategoryId; }
    public void setEventCategoryId(UUID eventCategoryId) { this.eventCategoryId = eventCategoryId; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public String getEventLocalisation() { return eventLocalisation; }
    public void setEventLocalisation(String eventLocalisation) { this.eventLocalisation = eventLocalisation; }

    public AskingStatus getStatus() { return status; }
    public void setStatus(AskingStatus status) { this.status = status; }
}
