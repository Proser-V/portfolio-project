package com.atelierlocal.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EventCategoryResponseDTO {

    private UUID id;
    private String name;
    private List<UUID> artisanCategoryIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EventCategoryResponseDTO(UUID id, String name, List<UUID> artisanCategoryIds,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.artisanCategoryIds = artisanCategoryIds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public List<UUID> getArtisanCategoryIds() { return artisanCategoryIds; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
