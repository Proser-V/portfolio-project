package com.atelierlocal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.ArtisanCategoryRequestDTO;
import com.atelierlocal.dto.ArtisanCategoryResponseDTO;
import com.atelierlocal.dto.ArtisanResponseDTO;
import com.atelierlocal.model.Client;
import com.atelierlocal.service.ArtisanCategoryService;
import com.atelierlocal.service.ArtisanService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/artisan-category")
@Tag(name = "Artisan Categories", description = "Définition du controlleur des catégories d'artisans")
public class ArtisanCategoryController {
    
    private final ArtisanCategoryService artisanCategoryService;
    private final ArtisanService artisanService;

    public ArtisanCategoryController(ArtisanCategoryService artisanCategoryService, ArtisanService artisanService) {
        this.artisanCategoryService = artisanCategoryService;
        this.artisanService = artisanService;
    }

    // --------------------
    // CRÉATION / MISE À JOUR / SUPPRESSION
    // --------------------

    /** 
     * Création d'une nouvelle catégorie d'artisan
     * Accessible uniquement aux ADMIN
     */
    @PostMapping("/creation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArtisanCategoryResponseDTO> createArtisanCategory(@Valid @RequestBody ArtisanCategoryRequestDTO request) {
        ArtisanCategoryResponseDTO newArtisanCategory = artisanCategoryService.createArtisanCategory(request);
        return ResponseEntity.ok(newArtisanCategory);
    }

    /**
     * Mise à jour d'une catégorie
     * Accessible uniquement aux ADMIN
     */
    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArtisanCategoryResponseDTO> updateArtisanCategory(
            @Valid @PathVariable UUID id,
            @RequestBody ArtisanCategoryRequestDTO request) {
        ArtisanCategoryResponseDTO updatedArtisanCategory = artisanCategoryService.updateArtisanCategory(id, request);
        return ResponseEntity.ok(updatedArtisanCategory);
    }

    /**
     * Suppression d'une catégorie
     * Accessible uniquement aux ADMIN
     */
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteArtisanCategory(@PathVariable UUID id) {
        artisanCategoryService.deleteArtisanCategory(id);
        return ResponseEntity.noContent().build();
    }

    // --------------------
    // LECTURE
    // --------------------

    /**
     * Récupère toutes les catégories d'artisans
     * Route publique : accessible à tout le monde
     */
    @GetMapping("/")
    public ResponseEntity<List<ArtisanCategoryResponseDTO>> getAllArtisanCategories() {
        List<ArtisanCategoryResponseDTO> allArtisanCategories = artisanCategoryService.getAllArtisanCategory();
        return ResponseEntity.ok(allArtisanCategories);
    }

    /**
     * Récupère une catégorie par son ID
     * Route publique : accessible à tout le monde
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArtisanCategoryResponseDTO> getArtisanCategoryById(@PathVariable UUID id) {
        ArtisanCategoryResponseDTO artisanCategory = artisanCategoryService.getArtisanCategoryById(id);
        return ResponseEntity.ok(artisanCategory);
    }

    /**
     * Récupère tous les artisans d'une catégorie
     * Accessible aux utilisateurs connectés (CLIENT, ARTISAN, ADMIN)
     */
    @GetMapping("/{id}/artisans")
    @PreAuthorize("hasAnyRole('CLIENT','ARTISAN','ADMIN')")
    public ResponseEntity<List<ArtisanResponseDTO>> getArtisansByCategory(
            @PathVariable UUID id,
            @AuthenticationPrincipal Client currentClient) {
        List<ArtisanResponseDTO> artisansByCategory = artisanService.getAllArtisansByCategory(id, currentClient);
        return ResponseEntity.ok(artisansByCategory);
    }
}
