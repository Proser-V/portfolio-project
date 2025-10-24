package com.atelierlocal.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EventCategoryRequestDTO {

    // Attributs

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "La catégorie ne peut pas dépasser 50 caractères")
    private String name;

    private List<UUID> artisanCategoryIds;

    // Getters + setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<UUID> getArtisanCategoryIds() { return artisanCategoryIds; }
    public void setArtisanCategoryList(List<UUID> artisanCategoryIds) { this.artisanCategoryIds = artisanCategoryIds; }
}
