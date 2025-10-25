package com.atelierlocal.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant une catégorie d'événement.
 * 
 * Cette classe permet de définir les types d'événements et leurs relations :
 * - nom de la catégorie
 * - catégories d'artisans associées (ManyToMany)
 * - demandes (askings) associées
 * - dates de création et de mise à jour automatiques
 */
@Entity
@Table(name = "event_categories")
public class EventCategory {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /**
     * Identifiant unique de la catégorie d'événement.
     * Généré automatiquement et non modifiable.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /**
     * Nom de la catégorie d'événement (max 50 caractères).
     */
    @Size(max = 50, message = "La catégorie ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * Liste des catégories d'artisans associées à cette catégorie d'événement.
     * Relation ManyToMany avec table de jointure event_artisan_category.
     */
    @ManyToMany
    @JoinTable(
        name = "event_artisan_category",
        joinColumns = @JoinColumn(name = "event_category_id"),
        inverseJoinColumns = @JoinColumn(name = "artisan_category_id")
    )
    private List<ArtisanCategory> artisanCategoryList = new ArrayList<>();

    /**
     * Liste des demandes (askings) associées à cette catégorie d'événement.
     * Relation OneToMany vers Asking, cascade sur toutes les opérations.
     */
    @OneToMany(mappedBy = "eventCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asking> askingsList = new ArrayList<>();

    /**
     * Date et heure de création de la catégorie d'événement.
     * Remplie automatiquement lors de l'insertion.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date et heure de la dernière mise à jour de la catégorie d'événement.
     * Mise à jour automatiquement à chaque modification.
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

    public List<ArtisanCategory> getArtisanCategoryList() { return artisanCategoryList; }
    public void setArtisanCategoryList(List<ArtisanCategory> artisanCategoryList) { this.artisanCategoryList = artisanCategoryList; }

    public List<Asking> getAskingsList() { return askingsList; }
    public void setAskingsList(List<Asking> askingsList) { this.askingsList = askingsList; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
