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

    // Getters et setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    public UUID getArtisanId() { return artisanId; }
    public void setArtisanId(UUID artisanId) { this.artisanId = artisanId; }
}
