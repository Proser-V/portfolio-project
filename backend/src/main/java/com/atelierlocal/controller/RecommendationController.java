package com.atelierlocal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.RecommendationResponseDTO;
import com.atelierlocal.model.Client;
import com.atelierlocal.service.RecommendationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrôleur REST pour la gestion des recommandations.
 * 
 * Ce contrôleur expose des endpoints permettant :
 * - De récupérer toutes les recommandations (admin uniquement)
 * - De récupérer une recommandation par son ID
 * - De supprimer une recommandation (admin uniquement)
 * 
 * Les endpoints sont sécurisés par rôle et certains sont accessibles uniquement aux administrateurs.
 */
@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendations", description = "Routes API gestion des recommendations")
public class RecommendationController {

    // Service métier dédié aux recommandations
    private final RecommendationService recommendationService;

    /**
     * Constructeur avec injection du service RecommendationService.
     * 
     * @param recommendationService service pour la gestion des recommandations
     */
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    // -------------------------------------------------------------------------
    // RÉCUPÉRER TOUTES LES RECOMMANDATIONS (ADMIN)
    // -------------------------------------------------------------------------
    
    /**
     * Récupère la liste complète des recommandations.
     * 
     * Accessible uniquement aux administrateurs grâce à @PreAuthorize.
     * L'objet Client injecté (@AuthenticationPrincipal) peut servir pour un éventuel logging ou filtrage.
     * 
     * @param currentClient Utilisateur authentifié (admin)
     * @return ResponseEntity contenant la liste des recommandations
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupère toutes les recommandations", description = "Accessible uniquement aux admins")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des recommandations récupérée"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<RecommendationResponseDTO>> getAllRecommendations(@AuthenticationPrincipal Client currentClient) {
        return ResponseEntity.ok(recommendationService.getAllRecommendations(currentClient));
    }

    // -------------------------------------------------------------------------
    // RÉCUPÉRER UNE RECOMMANDATION PAR SON ID
    // -------------------------------------------------------------------------
    
    /**
     * Récupère une recommandation spécifique via son UUID.
     * 
     * Accessible à tous les utilisateurs ayant les droits de consultation.
     * Retourne un DTO contenant les informations de la recommandation.
     * 
     * @param id UUID de la recommandation à récupérer
     * @return ResponseEntity contenant la recommandation
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupère une recommandation par son ID", description = "Accessible à tous ceux qui peuvent la voir")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recommandation récupérée"),
        @ApiResponse(responseCode = "404", description = "Recommandation introuvable")
    })
    public ResponseEntity<RecommendationResponseDTO> getRecommendation(@PathVariable UUID id) {
        return ResponseEntity.ok(recommendationService.getRecommendation(id));
    }

    // -------------------------------------------------------------------------
    // SUPPRIMER UNE RECOMMANDATION (ADMIN)
    // -------------------------------------------------------------------------
    
    /**
     * Supprime une recommandation identifiée par son UUID.
     * 
     * Accessible uniquement aux administrateurs grâce à @PreAuthorize.
     * Utilise le service métier pour effectuer la suppression et renvoie un message de confirmation.
     * 
     * @param id UUID de la recommandation à supprimer
     * @param currentClient Utilisateur authentifié (admin)
     * @return ResponseEntity avec message de succès
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprime une recommandation", description = "Accessible uniquement aux admins")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recommandation supprimée avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "404", description = "Recommandation introuvable")
    })
    public ResponseEntity<?> deleteRecommendation(@PathVariable UUID id, @AuthenticationPrincipal Client currentClient) {
        recommendationService.deleteRecommendation(id, currentClient);
        return ResponseEntity.ok().body(
            java.util.Map.of("message", "Recommandation supprimée avec succès.")
        );
    }
}
