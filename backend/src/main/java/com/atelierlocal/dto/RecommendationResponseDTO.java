package com.atelierlocal.dto;

import java.util.UUID;

/**
 * DTO pour représenter une recommandation retournée par l'API.
 * 
 * Contient :
 * - L'identifiant de la recommandation (id)
 * - L'identifiant du client ayant fait la recommandation (clientId)
 * - L'identifiant de l'artisan recommandé (artisanId)
 * 
 * Ce DTO est utilisé pour renvoyer les informations d'une recommandation 
 * aux consommateurs de l'API de manière sécurisée et simplifiée.
 */
public class RecommendationResponseDTO {
    
    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------
    
    private UUID id;
    private UUID clientId;
    private UUID artisanId;

    // -------------------------------------------------------------------------
    // CONSTRUCTEUR
    // -------------------------------------------------------------------------
    
    public RecommendationResponseDTO(UUID id, UUID clientId, UUID artisanId) {
        this.id = id;
        this.clientId = clientId;
        this.artisanId = artisanId;
    }

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------
    
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    public UUID getArtisanId() { return artisanId; }
    public void setArtisanId(UUID artisanId) { this.artisanId = artisanId; }
}
