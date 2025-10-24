package com.atelierlocal.dto;

import java.util.UUID;

public class ArtisanCategoryResponseDTO {
    private UUID id;
    private String name;
    private String description;

    public ArtisanCategoryResponseDTO(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Getters et setters

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
