package com.atelierlocal.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la création ou la mise à jour d'une catégorie d'événement.
 * 
 * Ce DTO permet de transférer les informations nécessaires pour créer une nouvelle
 * catégorie d'événement, ainsi que les associations avec des catégories d'artisans
 * concernées.
 */
public class EventCategoryRequestDTO {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /**
     * Nom de la catégorie d'événement.
     * Obligatoire et limité à 50 caractères.
     */
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "La catégorie ne peut pas dépasser 50 caractères")
    private String name;

    /**
     * Liste des identifiants des catégories d'artisans associées à cette catégorie
     * d'événement. Cette association permet de lier les demandes d'événement aux
     * artisans pertinents.
     */
    private List<UUID> artisanCategoryIds;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<UUID> getArtisanCategoryIds() { return artisanCategoryIds; }
    public void setArtisanCategoryList(List<UUID> artisanCategoryIds) { this.artisanCategoryIds = artisanCategoryIds; }
}
