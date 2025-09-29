package com.atelierlocal.dto;

import java.util.UUID;

public class RecommendationResponseDTO {
    
    private UUID id;
    private UUID clientId;
    private UUID artisanId;

    public RecommendationResponseDTO(UUID id, UUID clientId, UUID artisanId) {
        this.id = id;
        this.clientId = clientId;
        this.artisanId = artisanId;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getClientId() { return clientId; }
    public UUID getArtisanId() { return artisanId; }
}
