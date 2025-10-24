package com.atelierlocal.dto;

import java.util.UUID;

import com.atelierlocal.model.Recommendation;

public class RecommendationDTO {
    private UUID id;
    private UUID clientId;
    private UUID artisanId;

    public RecommendationDTO(Recommendation recommendation) {
        this.id = recommendation.getId();
        this.clientId = recommendation.getClient().getId();
        this.artisanId = recommendation.getArtisan().getId();
    }

    public UUID getId() { return id; }
    public UUID getClientId() { return clientId; }
    public UUID getArtisanId() { return artisanId; }
}
