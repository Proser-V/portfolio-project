package com.atelierlocal.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;

/**
 * DTO pour la création d'une recommandation.
 * 
 * Contient :
 * - L'identifiant du client qui recommande un artisan (clientId)
 * 
 * Ce DTO est utilisé lors de la requête de création d'une recommandation via l'API,
 * avec validation pour s'assurer que le clientId est bien fourni.
 */
public class RecommendationRequestDTO {
    
    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------
    
    @NotNull
    private UUID clientId;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------
    
    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }
}
