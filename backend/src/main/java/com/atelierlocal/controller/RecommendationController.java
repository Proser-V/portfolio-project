package com.atelierlocal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
@Tag(name = "Recommendations", description = "Routes API gestion des recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

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

    @GetMapping("/{id}")
    @Operation(summary = "Récupère une recommandation par son ID", description = "Accessible à tous ceux qui peuvent la voir")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recommandation récupérée"),
        @ApiResponse(responseCode = "404", description = "Recommandation introuvable")
    })
    public ResponseEntity<RecommendationResponseDTO> getRecommendation(@PathVariable UUID id) {
        return ResponseEntity.ok(recommendationService.getRecommendation(id));
    }

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
