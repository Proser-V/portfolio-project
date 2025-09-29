package com.atelierlocal.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class RecommendationRequestDTO {
    
    @NotNull
    private UUID clientId;

    // Getters et setters
    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }
}
