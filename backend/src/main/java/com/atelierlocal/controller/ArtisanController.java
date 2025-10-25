package com.atelierlocal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.atelierlocal.dto.*;
import com.atelierlocal.model.*;
import com.atelierlocal.service.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Contrôleur REST pour la gestion des artisans.
 * Fournit des endpoints pour l'enregistrement, consultation, modification, suppression,
 * recommandations et gestion du portfolio des artisans.
 */
@RestController
@RequestMapping("/api/artisans")
@Tag(name = "Artisans", description = "Définition du contrôleur des artisans")
public class ArtisanController {
    
    private final ArtisanService artisanService;
    private final RecommendationService recommendationService;
    private final PortfolioService portfolioService;

    /**
     * Constructeur du contrôleur avec injection des services nécessaires.
     */
    public ArtisanController(ArtisanService artisanService, RecommendationService recommendationService, PortfolioService portfolioService) {
        this.artisanService = artisanService;
        this.recommendationService = recommendationService;
        this.portfolioService = portfolioService;
    }

    // --------------------
    // ENREGISTREMENT
    // --------------------

    /**
     * Enregistrement d'un nouvel artisan.
     * Route publique : pas besoin d'être connecté.
     * Accepte des données multipart pour inclure un avatar.
     *
     * @param request DTO contenant les informations de l'artisan
     * @param avatar photo d'avatar (optionnelle)
     * @return ResponseEntity avec le DTO de l'artisan créé
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Enregistrement d'un nouvel artisan", description = "Création d'un nouvel artisan via les données entrées")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Artisan créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide (données manquantes ou incorrectes)"),
        @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<ArtisanResponseDTO> registerArtisan(
            @Valid @RequestPart("artisan") ArtisanRequestDTO request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        request.setAvatar(avatar);
        ArtisanResponseDTO artisanDto = artisanService.createArtisan(request);
        return ResponseEntity.status(201).body(artisanDto);
    }

    // --------------------
    // PROFIL ARTISAN
    // --------------------

    /**
     * Récupère le profil de l'artisan actuellement connecté.
     * Accessible uniquement aux ARTISANS.
     *
     * @param currentArtisan artisan authentifié
     * @return ResponseEntity avec le DTO du profil
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('ARTISAN')")
    public ResponseEntity<ArtisanResponseDTO> getCurrentUser(@AuthenticationPrincipal Artisan currentArtisan) {
        ArtisanResponseDTO artisanDTO = artisanService.getArtisanById(currentArtisan.getId());
        return ResponseEntity.ok(artisanDTO);
    }

    /**
     * Récupère un artisan par son ID.
     * Lecture publique.
     *
     * @param id UUID de l'artisan
     * @return ResponseEntity avec le DTO de l'artisan
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArtisanResponseDTO> getArtisanById(@PathVariable UUID id) {
        ArtisanResponseDTO artisanDto = artisanService.getArtisanById(id);
        return ResponseEntity.ok(artisanDto);
    }

    /**
     * Récupère tous les artisans.
     * Lecture publique.
     *
     * @param currentClient client authentifié (optionnel)
     * @return ResponseEntity avec la liste des artisans
     */
    @GetMapping("/")
    public ResponseEntity<List<ArtisanResponseDTO>> getAllArtisans(@AuthenticationPrincipal Client currentClient) {
        List<ArtisanResponseDTO> allArtisans = artisanService.getAllArtisans(currentClient);
        return ResponseEntity.ok(allArtisans);
    }

    // --------------------
    // MODIFICATION / SUPPRESSION
    // --------------------

    /**
     * Mise à jour d'un artisan.
     * Accessible à l'ARTISAN (pour son propre profil) et ADMIN.
     *
     * @param id UUID de l'artisan à mettre à jour
     * @param request DTO contenant les nouvelles données
     * @param currentUser utilisateur authentifié
     * @return ResponseEntity avec le DTO mis à jour
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
     * Suppression d'un artisan.
     * Accessible uniquement aux ADMIN.
     *
     * @param id UUID de l'artisan à supprimer
     * @param currentClient client authentifié (optionnel)
     * @return ResponseEntity sans contenu (204)
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
     * Création d'une nouvelle recommandation pour un artisan.
     * Accessible uniquement aux CLIENTS.
     *
     * @param id UUID de l'artisan recommandé
     * @param request DTO contenant les informations de la recommandation
     * @param currentClient client authentifié
     * @return ResponseEntity avec le DTO de la recommandation créée
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
     * Récupère 3 artisans aléatoires parmi les plus recommandés.
     * Route publique (ex : pour la home page).
     *
     * @return ResponseEntity avec la liste des DTO des artisans
     */
    @GetMapping("/random-top")
    public ResponseEntity<List<ArtisanResponseDTO>> getRandomTopArtisans() {
        List<Artisan> artisans = artisanService.getRandomTopArtisans(3);
        List<ArtisanResponseDTO> dtoList = artisans.stream()
            .map(ArtisanResponseDTO::new)
            .toList();
        return ResponseEntity.ok(dtoList);
    }

    // --------------------
    // PORTFOLIO
    // --------------------

    /**
     * Upload d'une photo dans le portfolio d'un artisan.
     * Accessible uniquement à l'ARTISAN.
     *
     * @param artisanId UUID de l'artisan
     * @param request DTO contenant le fichier à uploader
     * @param currentArtisan artisan authentifié
     * @return ResponseEntity avec le DTO de la photo uploadée
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
     * Suppression d'une photo du portfolio.
     * Accessible à l'ARTISAN (son propre portfolio) et ADMIN.
     *
     * @param artisanId UUID de l'artisan
     * @param photoId UUID de la photo
     * @param currentUser utilisateur authentifié
     * @return ResponseEntity sans contenu (204)
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
