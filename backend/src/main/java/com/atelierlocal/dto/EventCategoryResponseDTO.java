package com.atelierlocal.dto;

import java.util.List;
import java.util.UUID;

import com.atelierlocal.model.EventCategory;

/**
 * DTO de réponse pour une catégorie d'événement.
 * 
 * Ce DTO est utilisé pour exposer les informations d'une catégorie d'événement
 * via l'API, y compris son identifiant, son nom et les catégories d'artisans
 * associées.
 */
public class EventCategoryResponseDTO {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /** Identifiant unique de la catégorie d'événement */
    private UUID id;

    /** Nom de la catégorie d'événement */
    private String name;

    /**
     * Liste des identifiants des catégories d'artisans associées à cette catégorie
     * d'événement. Permet de connaître les relations entre événements et artisans.
     */
    private List<UUID> artisanCategoryIds;

    // -------------------------------------------------------------------------
    // CONSTRUCTEUR
    // -------------------------------------------------------------------------

    /**
     * Constructeur à partir d'une entité EventCategory.
     * 
     * @param eventCategory l'entité EventCategory à convertir en DTO
     */
    public EventCategoryResponseDTO(EventCategory eventCategory) {
        this.id = eventCategory.getId();
        this.name = eventCategory.getName();
        if (eventCategory.getArtisanCategoryList() != null) {
            this.artisanCategoryIds = eventCategory.getArtisanCategoryList()
                                            .stream()
                                            .map(ac -> ac.getId())
                                            .toList();
        } else {
            this.artisanCategoryIds = List.of();
        }
    }

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<UUID> getArtisanCategoryIds() { return artisanCategoryIds; }
    public void setArtisanCategoryIds(List<UUID> artisanCategoryIds) { this.artisanCategoryIds = artisanCategoryIds; }
}
