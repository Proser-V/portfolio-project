package com.atelierlocal.dto;

import java.util.UUID;

/**
 * DTO (Data Transfer Object) utilisé pour renvoyer les informations d'une
 * catégorie d'artisan via les endpoints REST.
 * 
 * Ce DTO est destiné à être envoyé au client et contient les informations
 * essentielles de la catégorie : son identifiant unique, son nom et sa description.
 */
public class ArtisanCategoryResponseDTO {

    /**
     * Identifiant unique de la catégorie.
     */
    private UUID id;

    /**
     * Nom de la catégorie.
     */
    private String name;

    /**
     * Description de la catégorie.
     */
    private String description;

    /**
     * Constructeur complet pour initialiser tous les champs du DTO.
     * 
     * @param id Identifiant unique de la catégorie
     * @param name Nom de la catégorie
     * @param description Description de la catégorie
     */
    public ArtisanCategoryResponseDTO(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
