package com.atelierlocal.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant un artisan.
 * 
 * Cette classe hérite de User et ajoute des informations spécifiques aux artisans :
 * - nom et bio
 * - catégorie d'artisan
 * - SIRET
 * - galerie de photos
 * - date de début d'activité
 * - recommandations reçues
 */
@Entity
@Table(name = "artisans")
public class Artisan extends User {
    
    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------
    
    /**
     * Nom de l'artisan (max 50 caractères).
     */
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * Description / bio de l'artisan (max 500 caractères).
     */
    @Size(max = 500, message = "La bio ne peut pas dépasser 500 caractères.")
    @Column(length = 500)
    private String bio;

    /**
     * Catégorie de l'artisan.
     * Relation ManyToOne vers ArtisanCategory.
     */
    @ManyToOne
    @JoinColumn(name = "artisan_category_name")
    @JsonManagedReference
    private ArtisanCategory category;

    /**
     * Numéro SIRET de l'artisan (14 chiffres).
     */
    @Column(length = 14)
    @Pattern(regexp = "\\d+", message = "Le champ ne doit contenir que des chiffres.")
    @Size(min = 14, max = 14)
    private String siret;

    /**
     * Galerie de photos de l'artisan.
     * Relation OneToMany vers UploadedPhoto, cascade sur toutes les opérations.
     */
    @OneToMany(mappedBy = "artisan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UploadedPhoto> photoGallery = new ArrayList<>();

    /**
     * Date de début d'activité de l'artisan.
     */
    private LocalDate activityStartDate;

    /**
     * Liste des recommandations reçues par l'artisan.
     * Relation OneToMany vers Recommendation, cascade sur toutes les opérations.
     */
    @OneToMany(mappedBy = "artisan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Recommendation> recommendations = new ArrayList<>();

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public ArtisanCategory getCategory() { return category; }
    public void setCategory(ArtisanCategory category) { this.category = category; }

    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }

    public List<UploadedPhoto> getPhotoGallery() { return photoGallery; }
    public void setPhotoGallery(List<UploadedPhoto> photoGallery) { this.photoGallery = photoGallery; }

    public LocalDate getActivityStartDate() { return activityStartDate; }
    public void setActivityStartDate(LocalDate activityStartDate) { this.activityStartDate = activityStartDate; }

    public List<Recommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<Recommendation> recommendations) { this.recommendations = recommendations; }
}
