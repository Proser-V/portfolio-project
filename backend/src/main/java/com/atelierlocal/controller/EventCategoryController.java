package com.atelierlocal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.ArtisanCategoryResponseDTO;
import com.atelierlocal.dto.EventCategoryRequestDTO;
import com.atelierlocal.dto.EventCategoryResponseDTO;
import com.atelierlocal.service.EventCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Contrôleur REST pour la gestion des catégories d'évènements.
 * Permet la création, mise à jour, suppression et consultation des catégories,
 * ainsi que la récupération des catégories d'artisans associées à chaque évènement.
 */
@RestController
@RequestMapping("/api/event-categories")
@Tag(name = "Event Categories", description = "API pour la gestion des catégories d'évènements")
public class EventCategoryController {

    private final EventCategoryService eventCategoryService;

    /**
     * Constructeur avec injection du service EventCategoryService.
     * 
     * @param eventCategoryService service de gestion des catégories d'évènements
     */
    public EventCategoryController(EventCategoryService eventCategoryService) {
        this.eventCategoryService = eventCategoryService;
    }

    // --------------------
    // CRÉATION
    // --------------------

    /**
     * Création d'une nouvelle catégorie d'évènement.
     * Accessible uniquement aux administrateurs.
     * 
     * @param request données de la catégorie à créer
     * @return ResponseEntity contenant la catégorie créée
     */
    @PostMapping("/creation")
    @Operation(summary = "Créer une catégorie d'évènement", description = "Accessible uniquement aux administrateurs")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catégorie créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide"),
        @ApiResponse(responseCode = "401", description = "Admin non authentifié"),
        @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    })
    public ResponseEntity<EventCategoryResponseDTO> createEventCategory(@Valid @RequestBody EventCategoryRequestDTO request) {
        EventCategoryResponseDTO newEventCategory = eventCategoryService.createEventCategory(request);
        return ResponseEntity.ok(newEventCategory);
    }

    // --------------------
    // LECTURE
    // --------------------

    /**
     * Récupère toutes les catégories d'évènements.
     * Accessible uniquement aux administrateurs.
     * 
     * @return liste de toutes les catégories
     */
    @GetMapping("/")
    @Operation(summary = "Lister toutes les catégories d'évènements", description = "Accessible uniquement aux administrateurs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
        @ApiResponse(responseCode = "401", description = "Admin non authentifié"),
        @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    })
    public ResponseEntity<List<EventCategoryResponseDTO>> getAllEventCategories() {
        List<EventCategoryResponseDTO> allEventCategories = eventCategoryService.getAllEventCategories();
        return ResponseEntity.ok(allEventCategories);
    }

    /**
     * Récupère une catégorie d'évènement par son ID.
     * Accessible uniquement aux administrateurs.
     * 
     * @param id identifiant de la catégorie
     * @return catégorie correspondant à l'ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une catégorie par ID", description = "Accessible uniquement aux administrateurs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catégorie récupérée"),
        @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    public ResponseEntity<EventCategoryResponseDTO> getEventCategoryById(@PathVariable UUID id) {
        EventCategoryResponseDTO eventCategory = eventCategoryService.getEventCategoryById(id);
        return ResponseEntity.ok(eventCategory);
    }

    /**
     * Récupère les catégories d'artisans associées à une catégorie d'évènement.
     * Accessible uniquement aux administrateurs.
     * 
     * @param id identifiant de l'évènement
     * @return liste des catégories d'artisans liées à l'évènement
     */
    @GetMapping("/{id}/artisan-categories")
    @Operation(summary = "Lister les catégories d'artisans associées à un évènement", description = "Accessible uniquement aux administrateurs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des catégories d'artisans récupérée"),
        @ApiResponse(responseCode = "404", description = "Evènement non trouvé")
    })
    public ResponseEntity<List<ArtisanCategoryResponseDTO>> getArtisanCategoriesByEvent(@PathVariable UUID id) {
        List<ArtisanCategoryResponseDTO> artisanCategoriesByEvent = eventCategoryService.getArtisanCategoriesByEvent(id);
        return ResponseEntity.ok(artisanCategoriesByEvent);
    }

    // --------------------
    // MISE À JOUR
    // --------------------

    /**
     * Mise à jour d'une catégorie d'évènement.
     * Accessible uniquement aux administrateurs.
     * 
     * @param id identifiant de la catégorie
     * @param request données à mettre à jour
     * @return catégorie mise à jour
     */
    @PutMapping("/{id}/update")
    @Operation(summary = "Mettre à jour une catégorie d'évènement", description = "Accessible uniquement aux administrateurs")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catégorie mise à jour"),
        @ApiResponse(responseCode = "400", description = "Requête invalide"),
        @ApiResponse(responseCode = "404", description = "Catégorie non trouvée"),
        @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    })
    public ResponseEntity<EventCategoryResponseDTO> updateEventCategory(
            @Valid @PathVariable UUID id,
            @RequestBody EventCategoryRequestDTO request
    ) {
        EventCategoryResponseDTO updatedEventCategory = eventCategoryService.updateEventCategory(id, request);
        return ResponseEntity.ok(updatedEventCategory);
    }

    // --------------------
    // SUPPRESSION
    // --------------------

    /**
     * Suppression d'une catégorie d'évènement.
     * Accessible uniquement aux administrateurs.
     * 
     * @param id identifiant de la catégorie à supprimer
     * @return ResponseEntity vide
     */
    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Supprimer une catégorie d'évènement", description = "Accessible uniquement aux administrateurs")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Catégorie supprimée"),
        @ApiResponse(responseCode = "404", description = "Catégorie non trouvée"),
        @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    })
    public ResponseEntity<Void> deleteEventCategory(@PathVariable UUID id) {
        eventCategoryService.deleteEventCategory(id);
        return ResponseEntity.noContent().build();
    }
}
