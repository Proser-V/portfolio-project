package com.atelierlocal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) utilisé pour la création ou la mise à jour
 * d'une catégorie d'artisan via les endpoints REST.
 * 
 * Ce DTO contient les informations saisies par l'utilisateur et applique
 * des contraintes de validation pour garantir l'intégrité des données.
 */
public class ArtisanCategoryRequestDTO {

    /**
     * Nom de la catégorie.
     * 
     * Contraintes de validation :
     * - Ne doit pas être vide (@NotBlank)
     * - Longueur maximale de 50 caractères (@Size(max = 50))
     */
    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères.")
    private String name;

    /**
     * Description de la catégorie.
     * 
     * Contraintes de validation :
     * - Ne doit pas être vide (@NotBlank)
     * - Longueur maximale de 50 caractères (@Size(max = 50))
     */
    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères.")
    private String description;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
