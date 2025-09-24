package com.atelierlocal.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AskingRequestDTO {
    // Attributs
    @NotBlank(message = "L'ID du client créateur est obligatoire.")
    private UUID clientId;

    @NotBlank(message = "Objet de la demande obligatoire.")
    @Size(max = 1000, message = "La demande ne peut pas dépasser 1000 caractères.")
    private String content;

    @NotBlank(message = "Une demande doit être liée à une catégorie d'artisan.")
    private UUID artisanCategoryId;

    private UUID eventCategoryId;

    private LocalDateTime eventDate;

    private String eventLocalisation;

    // Getters et setters

    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public UUID getArtisanCategoryId() { return artisanCategoryId; }
    public void setArtisanCategoryId(UUID artisanCategoryId) { this.artisanCategoryId = artisanCategoryId; }

    public UUID getEventCategoryId() { return eventCategoryId; }
    public void setEventCategoryId(UUID eventCategoryId) { this.eventCategoryId = eventCategoryId; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public String getEventLocalisation() { return eventLocalisation; }
    public void setEventLocalisation(String eventLocalisation) { this.eventLocalisation = eventLocalisation; }
}
