package com.atelierlocal.dto;

import java.util.List;
import java.util.UUID;

import com.atelierlocal.model.EventCategory;

public class EventCategoryResponseDTO {

    // Attributs

    private UUID id;
    private String name;
    private List<UUID> artisanCategoryIds;

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

    // Getters et setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<UUID> getArtisanCategoryIds() { return artisanCategoryIds; }
    public void setArtisanCategoryIds(List<UUID> artisanCategoryIds) { this.artisanCategoryIds = artisanCategoryIds; }
}
