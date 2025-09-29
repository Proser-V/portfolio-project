package com.atelierlocal.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.ArtisanResponseDTO;
import com.atelierlocal.dto.RecommendationDTO;
import com.atelierlocal.dto.ArtisanRequestDTO;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Recommendation;
import com.atelierlocal.repository.ArtisanCategoryRepo;
import com.atelierlocal.service.ArtisanService;
import com.atelierlocal.service.RecommendationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/artisans")
@Tag(name = "Artisans", description = "Définition du controlleur des artisans")
public class ArtisanController {
    private final ArtisanService artisanService;
    private final RecommendationService recommendationService;

    public ArtisanController(ArtisanService artisanService, RecommendationService recommendationService) {
        this.artisanService = artisanService;
        this.recommendationService = recommendationService;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Enregistrement d'un nouvel artisan", description = "Création d'un nouvel artisan via les données entrées")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Artisan créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide (données manquantes ou incorrectes)"),
        @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<ArtisanResponseDTO> registerArtisan(@Valid @ModelAttribute ArtisanRequestDTO request) {
        ArtisanResponseDTO artisanDto = artisanService.createArtisan(request);
        return ResponseEntity.status(201).body(artisanDto);
    }

    @GetMapping("/me")
    public ResponseEntity<ArtisanResponseDTO> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        ArtisanResponseDTO artisanDTO = artisanService.getArtisanByEmail(email);
        return ResponseEntity.ok(artisanDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtisanResponseDTO> getArtisanById(@PathVariable UUID id) {
        ArtisanResponseDTO artisanDto = artisanService.getArtisanById(id);
        return ResponseEntity.ok(artisanDto);
    }

    @GetMapping("/")
    public ResponseEntity<List<ArtisanResponseDTO>> getAllArtisans() {
        List<ArtisanResponseDTO> allArtisans = artisanService.getAllArtisans();
        return ResponseEntity.ok(allArtisans);
    }
    
    @PutMapping("/{id}/update")
    public ResponseEntity<ArtisanResponseDTO> updateArtisans(@PathVariable UUID id, @RequestBody ArtisanRequestDTO request) {
        ArtisanResponseDTO artisanDto = artisanService.updateArtisan(id, request);
        return ResponseEntity.ok(artisanDto);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteArtisan(@PathVariable UUID id) {
        artisanService.deleteArtisan(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/recommandation")
    public ResponseEntity<RecommendationResponseDTO> newRecommendation(UUID id, RecommendationRequestDTO request) {
        RecommendationResponseDTO newRecommendation = recommendationService.createRecommandation(id, request);
        return ResponseEntity.ok(newRecommendation);
    }
    
}
