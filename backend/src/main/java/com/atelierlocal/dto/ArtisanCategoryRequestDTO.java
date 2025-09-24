package com.atelierlocal.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ArtisanCategoryRequestDTO {
    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères.")
    private String name;

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères.")
    private String description;

    public ArtisanCategoryRequestDTO(String name, String description, List<UUID> eventCategoryIds) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
