package com.atelierlocal.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant une demande (Asking) effectuée par un client.
 * 
 * Une demande est liée à un client, à une catégorie d'artisan, et éventuellement à une
 * catégorie d'événement. Elle contient des informations sur le titre, le contenu, la localisation,
 * la date de l'événement, et l'état de la demande.
 */
@Entity
@Table(name = "askings")
public class Asking {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /**
     * Identifiant unique de la demande.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Titre de la demande (max 50 caractères).
     */
    @Size(max = 50, message = "Le titre ne peut excéder 50 caractères.")
    @Column(nullable = false, length = 50)
    private String title;

    /**
     * Contenu détaillé de la demande (max 1000 caractères).
     */
    @Size(max = 1000, message = "La demande ne peux excéder 1000 caractères.")
    @Column(nullable = false, length = 1000)
    private String content;

    /**
     * Catégorie d'événement associée à la demande (optionnelle).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_category_id", nullable = true)
    private EventCategory eventCategory;

    /**
     * Localisation de l'événement ou de la demande (nom de la ville, max 100 caractères).
     */
    @Size(max = 100, message = "Le nom de la ville ne peut excéder 100 caractères.")
    @Column(length = 100)
    private String eventLocalisation;

    /**
     * Date et heure prévue pour l'événement (optionnelle).
     */
    @Column
    private LocalDateTime eventDate;

    /**
     * Client ayant créé la demande.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    /**
     * Catégorie d'artisan ciblée par la demande.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artisan_category_id", nullable = false)
    private ArtisanCategory artisanCategory;

    /**
     * Statut actuel de la demande (enum AskingStatus).
     */
    @Enumerated(EnumType.STRING)
    private AskingStatus status;

    /**
     * Date et heure de création de la demande. Remplie automatiquement à la création.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date et heure de dernière mise à jour de la demande. Remplie automatiquement à chaque modification.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public ArtisanCategory getArtisanCategory() { return artisanCategory; }
    public void setArtisanCategory(ArtisanCategory artisanCategory) { this.artisanCategory = artisanCategory; }

    public EventCategory getEventCategory() { return eventCategory; }
    public void setEventCategory(EventCategory eventCategory) { this.eventCategory = eventCategory; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public String getEventLocalisation() { return eventLocalisation; }
    public void setEventLocalisation(String eventLocalisation) { this.eventLocalisation = eventLocalisation; }

    public AskingStatus getStatus() { return status; }
    public void setStatus(AskingStatus status) { this.status = status; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
