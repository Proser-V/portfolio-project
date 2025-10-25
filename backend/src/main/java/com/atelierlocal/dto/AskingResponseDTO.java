package com.atelierlocal.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.atelierlocal.model.Asking;
import com.atelierlocal.model.AskingStatus;

/**
 * DTO (Data Transfer Object) utilisé pour renvoyer les informations d'une
 * demande (Asking) côté client ou dans l'API.
 * 
 * Ce DTO contient toutes les informations pertinentes pour visualiser
 * une demande, y compris :
 * - les informations sur le client
 * - les détails de la demande et de l'événement
 * - le statut de la demande
 * - la date de création
 */
public class AskingResponseDTO {

    /**
     * Identifiant unique de la demande
     */
    private UUID id;

    /**
     * Identifiant du client ayant créé la demande
     */
    private UUID clientId;

    /**
     * Titre de la demande
     */
    private String title;

    /**
     * Contenu détaillé de la demande
     */
    private String content;

    /**
     * Identifiant de la catégorie d'artisan associée à la demande
     */
    private UUID artisanCategoryId;

    /**
     * Identifiant de la catégorie d'événement associée à la demande (optionnel)
     */
    private UUID eventCategoryId;

    /**
     * Date et heure de l'événement (optionnel)
     */
    private LocalDateTime eventDate;

    /**
     * Localisation de l'événement (optionnel)
     */
    private String eventLocalisation;

    /**
     * Statut actuel de la demande (PENDING, DONE, CANCELLED)
     */
    private AskingStatus status;

    /**
     * Date de création de la demande
     */
    private LocalDateTime createdAt;

    /**
     * Constructeur à partir d'une entité Asking.
     * 
     * Ce constructeur initialise tous les champs du DTO à partir de
     * l'entité persistée, avec gestion des champs optionnels.
     * 
     * @param asking l'entité Asking à transformer en DTO
     */
    public AskingResponseDTO(Asking asking) {
        this.id = asking.getId();
        this.clientId = asking.getClient().getId();
        this.title = asking.getTitle();
        this.content = asking.getContent();
        this.artisanCategoryId = asking.getArtisanCategory().getId();
        this.eventCategoryId = asking.getEventCategory() != null ? asking.getEventCategory().getId() : null;
        this.eventDate = asking.getEventDate() != null ? asking.getEventDate() : null;
        this.eventLocalisation = asking.getEventLocalisation() != null ? asking.getEventLocalisation() : null;
        this.status = asking.getStatus();
        this.createdAt = asking.getCreatedAt();
    }

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }

    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

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

    public AskingStatus getStatus() { return status; }
    public void setStatus(AskingStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
