package com.atelierlocal.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant une catégorie d'artisan.
 * 
 * Une catégorie permet de regrouper plusieurs artisans et peut être associée à plusieurs
 * catégories d'événements et demandes (askings). Elle contient également les dates de
 * création et de mise à jour automatiquement gérées.
 */
@Entity
@Table(name = "artisan_categories")
public class ArtisanCategory {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /**
     * Identifiant unique de la catégorie.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /**
     * Nom de la catégorie (unique, max 50 caractères).
     */
    @Size(max = 50, message = "La catégorie ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50, unique = true)
    private String name;

    /**
     * Description de la catégorie (max 200 caractères).
     */
    @Size(max = 200, message = "La description ne peut pas dépasser 200 caractères.")
    @Column(nullable = false, length = 200)
    private String description;

    /**
     * Liste des artisans appartenant à cette catégorie.
     * Relation OneToMany vers Artisan, côté inverse de la relation.
     */
    @OneToMany(mappedBy = "category")
    @JsonBackReference
    private List<Artisan> artisanList = new ArrayList<>();

    /**
     * Liste des catégories d'événements associées à cette catégorie d'artisan.
     * Relation ManyToMany côté inverse.
     */
    @ManyToMany(mappedBy = "artisanCategoryList")
    @JsonIgnore
    private List<EventCategory> eventCategories = new ArrayList<>();

    /**
     * Liste des demandes (askings) liées à cette catégorie.
     * Relation OneToMany vers Asking.
     */
    @OneToMany(mappedBy = "artisanCategory")
    @JsonIgnore
    private List<Asking> askingsList = new ArrayList<>();

    /**
     * Date de création de la catégorie. Remplie automatiquement à la création.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date de dernière mise à jour de la catégorie. Remplie automatiquement à chaque modification.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Artisan> getArtisanList() { return artisanList; }
    public void setArtisanList(List<Artisan> artisanList) { this.artisanList = artisanList; }

    public List<EventCategory> getEventCategories() { return eventCategories; }
    public void setEventCategories(List<EventCategory> eventCategories) { this.eventCategories = eventCategories;}

    public List<Asking> getAskingsList() { return this.askingsList; }
    public void setAskingsList(List<Asking> askingsList) { this.askingsList = askingsList; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
