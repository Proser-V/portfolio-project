package com.atelierlocal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.atelierlocal.dto.*;
import com.atelierlocal.model.*;
import com.atelierlocal.service.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/artisans")
@Tag(name = "Artisans", description = "Définition du controlleur des artisans")
public class ArtisanController {
    
    private final ArtisanService artisanService;
    private final RecommendationService recommendationService;
    private final PortfolioService portfolioService;

    public ArtisanController(ArtisanService artisanService, RecommendationService recommendationService, PortfolioService portfolioService) {
        this.artisanService = artisanService;
        this.recommendationService = recommendationService;
        this.portfolioService = portfolioService;
    }

    // --------------------
    // ENREGISTREMENT
    // --------------------
    
    /**
     * Enregistrement d'un nouvel artisan
     * Route publique : pas besoin d'être connecté
     */
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

    // --------------------
    // PROFIL ARTISAN
    // --------------------
    
    /**
     * Récupère le profil de l'artisan connecté
     * Accessible uniquement à l'ARTISAN (ou ADMIN si besoin)
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('ARTISAN')")
    public ResponseEntity<ArtisanResponseDTO> getCurrentUser(@AuthenticationPrincipal Artisan currentArtisan) {
        ArtisanResponseDTO artisanDTO = artisanService.getArtisanById(currentArtisan.getId());
        return ResponseEntity.ok(artisanDTO);
    }

    /**
     * Récupère un artisan par son ID
     * Lecture publique : pas de restriction
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArtisanResponseDTO> getArtisanById(@PathVariable UUID id) {
        ArtisanResponseDTO artisanDto = artisanService.getArtisanById(id);
        return ResponseEntity.ok(artisanDto);
    }

    /**
     * Récupère tous les artisans
     * Accessible aux CLIENTS et ADMIN
     */
    @GetMapping("/")
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public ResponseEntity<List<ArtisanResponseDTO>> getAllArtisans(@AuthenticationPrincipal Client currentClient) {
        List<ArtisanResponseDTO> allArtisans = artisanService.getAllArtisans(currentClient);
        return ResponseEntity.ok(allArtisans);
    }

    // --------------------
    // MODIFICATION / SUPPRESSION
    // --------------------
    
    /**
     * Mise à jour d'un artisan
     * Accessible à l'ARTISAN (pour son propre profil) et ADMIN
     */
    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('ARTISAN','ADMIN')")
    public ResponseEntity<ArtisanResponseDTO> updateArtisan(
            @Valid @PathVariable UUID id, 
            @RequestBody ArtisanRequestDTO request, 
            @AuthenticationPrincipal User currentUser) {
        ArtisanResponseDTO artisanDto = artisanService.updateArtisan(id, request, currentUser);
        return ResponseEntity.ok(artisanDto);
    }

    /**
     * Suppression d'un artisan
     * Accessible uniquement aux ADMIN
     */
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteArtisan(
            @PathVariable UUID id, 
            @AuthenticationPrincipal Client currentClient) {
        artisanService.deleteArtisan(id, currentClient);
        return ResponseEntity.noContent().build();
    }

    // --------------------
    // RECOMMANDATIONS
    // --------------------
    
    /**
     * Création d'une nouvelle recommandation pour un artisan
     * Accessible uniquement aux CLIENTS
     */
    @PostMapping("/{id}/recommandation")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<RecommendationResponseDTO> newRecommendation(
            @Valid @PathVariable UUID id, 
            @RequestBody RecommendationRequestDTO request, 
            @AuthenticationPrincipal Client currentClient) {
        RecommendationResponseDTO newRecommendation = recommendationService.createRecommendation(id, request, currentClient);
        return ResponseEntity.ok(newRecommendation);
    }

    /**
     * Récupération de 3 artisans aléatoires parmis les plus recommandés.
     * Route publique : pas besoin d'être connecté (home page)
     */
    @GetMapping("/random-top")
    public ResponseEntity<List<Artisan>> getRandomTopArtisans() {
        List<Artisan> artisans = artisanService.getRandomTopArtisans(3);
        return ResponseEntity.ok(artisans);
    }

    // --------------------
    // PORTFOLIO
    // --------------------
    
    /**
     * Upload d'une photo dans le portfolio
     * Accessible uniquement à l'ARTISAN
     */
    @PostMapping("/{id}/portfolio/upload")
    @PreAuthorize("hasRole('ARTISAN')")
    public ResponseEntity<UploadedPhotoResponseDTO> uploadPortfolioPhoto(
            @Valid @PathVariable("id") UUID artisanId, 
            @ModelAttribute UploadedPhotoRequestDTO request, 
            @AuthenticationPrincipal Artisan currentArtisan) {
        UploadedPhoto photo = portfolioService.addPhoto(artisanId, request.getFile(), currentArtisan);
        return ResponseEntity.ok(new UploadedPhotoResponseDTO(photo));
    }

    /**
     * Suppression d'une photo du portfolio
     * Accessible à l'ARTISAN (son propre portfolio) et ADMIN
     */
    @DeleteMapping("/{artisanId}/portfolio/{photoId}/delete")
    @PreAuthorize("hasAnyRole('ARTISAN','ADMIN')")
    public ResponseEntity<Void> deletePortfolioPhoto(
            @PathVariable UUID artisanId,
            @PathVariable UUID photoId,
            @AuthenticationPrincipal User currentUser) {
        portfolioService.removePhoto(artisanId, photoId, currentUser);
        return ResponseEntity.noContent().build();
    } 
}
