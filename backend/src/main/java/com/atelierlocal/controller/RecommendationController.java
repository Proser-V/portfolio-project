package com.atelierlocal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.RecommendationResponseDTO;
import com.atelierlocal.service.RecommendationService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/recommmendations")
@Tag(name = "Recommendations", description = "Routes API gestion des recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<RecommendationResponseDTO>> getAllRecommendations() {
        return ResponseEntity.ok(recommendationService.getAllRecommendations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationResponseDTO> getRecommendation(@PathVariable UUID id) {
        return ResponseEntity.ok(recommendationService.getRecommendation(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecommendation(@PathVariable UUID id) {
        recommendationService.deleteRecommendation(id);
        return ResponseEntity.ok().body(
            java.util.Map.of("message", "Recommandation supprimée avec succès.")
        );
    }
}
