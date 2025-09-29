package com.atelierlocal.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class RecommendationResponseDTO {
    
    private UUID id;
    private UUID clientId;
    private UUID artisanId;
    private LocalDateTime createdAt;

    public RecommendationResponseDTO(UUID id, UUID clientId, UUID artisanId, LocalDateTime createdAt) {
        this.id = id;
        this.clientId = clientId;
        this.artisanId = artisanId;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getClientId() { return clientId; }
    public UUID getArtisanId() { return artisanId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
