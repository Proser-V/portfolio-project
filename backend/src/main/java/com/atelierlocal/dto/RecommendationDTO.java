package com.atelierlocal.dto;

import java.util.UUID;
import com.atelierlocal.model.Recommendation;

/**
 * DTO de réponse pour représenter une recommandation faite par un client à un artisan.
 * 
 * Contient :
 * - L'identifiant unique de la recommandation (id)
 * - L'identifiant du client ayant fait la recommandation (clientId)
 * - L'identifiant de l'artisan recommandé (artisanId)
 * 
 * Ce DTO est principalement utilisé pour exposer les données de recommandations via l'API
 * sans exposer directement les entités JPA.
 */
public class RecommendationDTO {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------
    
    private UUID id;
    private UUID clientId;
    private UUID artisanId;

    // -------------------------------------------------------------------------
    // CONSTRUCTEUR
    // -------------------------------------------------------------------------

    /**
     * Initialise le DTO à partir d'une entité Recommendation.
     * Récupère les identifiants du client et de l'artisan associés.
     */
    public RecommendationDTO(Recommendation recommendation) {
        this.id = recommendation.getId();
        this.clientId = recommendation.getClient().getId();
        this.artisanId = recommendation.getArtisan().getId();
    }

    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }
    public UUID getClientId() { return clientId; }
    public UUID getArtisanId() { return artisanId; }
}
