package com.atelierlocal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.ArtisanRequestDTO;
import com.atelierlocal.dto.ArtisanResponseDTO;
import com.atelierlocal.dto.RecommendationRequestDTO;
import com.atelierlocal.dto.RecommendationResponseDTO;
import com.atelierlocal.dto.UploadedPhotoRequestDTO;
import com.atelierlocal.dto.UploadedPhotoResponseDTO;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.UploadedPhoto;
import com.atelierlocal.model.User;
import com.atelierlocal.service.ArtisanService;
import com.atelierlocal.service.PortfolioService;
import com.atelierlocal.service.RecommendationService;

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
    @PreAuthorize("hasRole('ARTISAN')")
    public ResponseEntity<ArtisanResponseDTO> getCurrentUser(@AuthenticationPrincipal Artisan currentArtisan) {
        ArtisanResponseDTO artisanDTO = artisanService.getArtisanById(currentArtisan.getId());
        return ResponseEntity.ok(artisanDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtisanResponseDTO> getArtisanById(@PathVariable UUID id) {
        ArtisanResponseDTO artisanDto = artisanService.getArtisanById(id);
        return ResponseEntity.ok(artisanDto);
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<List<ArtisanResponseDTO>> getAllArtisans(@AuthenticationPrincipal Client currentClient) {
        List<ArtisanResponseDTO> allArtisans = artisanService.getAllArtisans(currentClient);
        return ResponseEntity.ok(allArtisans);
    }
    
    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('ARTISAN', 'ADMIN')")
    public ResponseEntity<ArtisanResponseDTO> updateArtisan(@Valid @PathVariable UUID id, @RequestBody ArtisanRequestDTO request, @AuthenticationPrincipal User currentUser) {
        ArtisanResponseDTO artisanDto = artisanService.updateArtisan(id, request, currentUser);
        return ResponseEntity.ok(artisanDto);
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteArtisan(@PathVariable UUID id, @AuthenticationPrincipal Client currentClient) {
        artisanService.deleteArtisan(id, currentClient);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/recommandation")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<RecommendationResponseDTO> newRecommendation(@Valid @PathVariable UUID id, @RequestBody RecommendationRequestDTO request, @AuthenticationPrincipal Client currentClient) {
        RecommendationResponseDTO newRecommendation = recommendationService.createRecommendation(id, request, currentClient);
        return ResponseEntity.ok(newRecommendation);
    }
    
    @PostMapping("/{id}/portfolio/upload")
    @PreAuthorize("hasRole('ARTISAN')")
    public ResponseEntity<UploadedPhotoResponseDTO> uploadPortfolioPhoto(@Valid @PathVariable("id") UUID artisanId, @ModelAttribute UploadedPhotoRequestDTO request, @AuthenticationPrincipal Artisan currentArtisan) {
        UploadedPhoto photo = portfolioService.addPhoto(artisanId, request.getFile(), currentArtisan);
        return ResponseEntity.ok(new UploadedPhotoResponseDTO(photo));
    }

    @DeleteMapping("/{artisanId}/portfolio/{photoId}/delete")
    @PreAuthorize("hasAnyRole('ARTISAN', 'ADMIN')")
    public ResponseEntity<Void> deletePortfolioPhoto(
                                            @PathVariable UUID artisanId,
                                            @PathVariable UUID photoId,
                                            @AuthenticationPrincipal User currentUser) {
        portfolioService.removePhoto(artisanId, photoId, currentUser);
        return ResponseEntity.noContent().build();
    } 
}
