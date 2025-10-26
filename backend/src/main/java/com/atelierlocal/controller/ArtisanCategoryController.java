package com.atelierlocal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.ArtisanCategoryRequestDTO;
import com.atelierlocal.dto.ArtisanCategoryResponseDTO;
import com.atelierlocal.dto.ArtisanResponseDTO;
import com.atelierlocal.dto.AskingResponseDTO;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.User;
import com.atelierlocal.service.ArtisanCategoryService;
import com.atelierlocal.service.ArtisanService;
import com.atelierlocal.service.AskingService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Contrôleur REST pour la gestion des catégories d'artisans.
 * Fournit des endpoints pour la création, mise à jour, suppression et consultation
 * des catégories ainsi que pour récupérer les artisans ou askings associés.
 */
@RestController
@RequestMapping("/api/artisan-category")
@Tag(name = "Artisan Categories", description = "Définition du contrôleur des catégories d'artisans")
public class ArtisanCategoryController {
    
    private final ArtisanCategoryService artisanCategoryService;
    private final ArtisanService artisanService;
    private final AskingService askingService;

    /**
     * Constructeur du contrôleur.
     * Injection des services nécessaires pour gérer les catégories, artisans et askings.
     */
    public ArtisanCategoryController(
        ArtisanCategoryService artisanCategoryService,
        ArtisanService artisanService,
        AskingService askingService
        ) {
        this.artisanCategoryService = artisanCategoryService;
        this.artisanService = artisanService;
        this.askingService = askingService;
    }

    // --------------------
    // CRÉATION / MISE À JOUR / SUPPRESSION
    // --------------------

    /**
     * Création d'une nouvelle catégorie d'artisan.
     * Accessible uniquement aux utilisateurs avec le rôle ADMIN.
     * 
     * @param request DTO contenant les informations de la nouvelle catégorie
     * @return ResponseEntity avec le DTO de la catégorie créée
     */
    @PostMapping("/creation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArtisanCategoryResponseDTO> createArtisanCategory(@Valid @RequestBody ArtisanCategoryRequestDTO request) {
        ArtisanCategoryResponseDTO newArtisanCategory = artisanCategoryService.createArtisanCategory(request);
        return ResponseEntity.ok(newArtisanCategory);
    }

    /**
     * Mise à jour d'une catégorie existante.
     * Accessible uniquement aux ADMIN.
     * 
     * @param id UUID de la catégorie à mettre à jour
     * @param request DTO avec les nouvelles données
     * @return ResponseEntity avec le DTO de la catégorie mise à jour
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
     * Suppression d'une catégorie.
     * Accessible uniquement aux ADMIN.
     * 
     * @param id UUID de la catégorie à supprimer
     * @return ResponseEntity sans contenu (204)
     */
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteArtisanCategory(@PathVariable UUID id) {
        artisanCategoryService.deleteArtisanCategory(id);
        return ResponseEntity.noContent().build();
    }

    // --------------------
    // LECTURE / CONSULTATION
    // --------------------

    /**
     * Récupère toutes les catégories d'artisans.
     * Route publique : accessible à tous.
     * 
     * @return ResponseEntity avec la liste des catégories
     */
    @GetMapping("/")
    public ResponseEntity<List<ArtisanCategoryResponseDTO>> getAllArtisanCategories() {
        List<ArtisanCategoryResponseDTO> allArtisanCategories = artisanCategoryService.getAllArtisanCategory();
        return ResponseEntity.ok(allArtisanCategories);
    }

    /**
     * Récupère une catégorie par son ID.
     * Route publique : accessible à tous.
     * 
     * @param id UUID de la catégorie
     * @return ResponseEntity avec le DTO de la catégorie
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArtisanCategoryResponseDTO> getArtisanCategoryById(@PathVariable UUID id) {
        ArtisanCategoryResponseDTO artisanCategory = artisanCategoryService.getArtisanCategoryById(id);
        return ResponseEntity.ok(artisanCategory);
    }

    /**
     * Récupère tous les artisans appartenant à une catégorie spécifique.
     * Route publique : accessible à tous.
     * 
     * @param id UUID de la catégorie
     * @param currentClient client actuellement authentifié (peut être null si non connecté)
     * @return ResponseEntity avec la liste des artisans
     */
    @GetMapping("/{id}/artisans")
    public ResponseEntity<List<ArtisanResponseDTO>> getArtisansByCategory(
            @PathVariable UUID id,
            @AuthenticationPrincipal Client currentClient) {
        List<ArtisanResponseDTO> artisansByCategory = artisanService.getAllArtisansByCategory(id, currentClient);
        return ResponseEntity.ok(artisansByCategory);
    }

    /**
     * Récupère tous les askings liés à une catégorie.
     * Accessible à tous les utilisateurs.
     * 
     * @param id UUID de la catégorie
     * @param currentUser utilisateur actuellement authentifié (peut être null si non connecté)
     * @return ResponseEntity avec la liste des askings
     */
    @GetMapping("/{id}/askings")
    // @PreAuthorize("hasAnyRole('ARTISAN', 'ADMIN')")
    public ResponseEntity<List<AskingResponseDTO>> getAskingsByCategory(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        List<AskingResponseDTO> askingsByCategory = askingService.getAskingsByCategory(id, currentUser);
        return ResponseEntity.ok(askingsByCategory);
    }
}
