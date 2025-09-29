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


@Entity
@Table(name = "askings")
public class Asking {
    // Atributs

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Size(max = 50, message = "Le titre ne peut excéder 50 caractères.")
    @Column(nullable = false, length = 50)
    private String title;

    @Size(max = 1000, message = "La demande ne peux excéder 1000 caractères.")
    @Column(nullable = false, length = 1000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_category_id", nullable = true)
    private EventCategory eventCategory;

    @Size(max = 100, message = "Le nom de la ville ne peut excéder 100 caractères.")
    @Column(length = 100)
    private String eventLocalisation;

    @Column
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artisan_category_id", nullable = false)
    private ArtisanCategory artisanCategory;

    @Enumerated(EnumType.STRING)
    private AskingStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Getters et setters

    public UUID getId() { return id; }

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
