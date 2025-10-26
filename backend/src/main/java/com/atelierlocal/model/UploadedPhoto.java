package com.atelierlocal.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entité représentant une photo uploadée par un artisan.
 * 
 * Cette classe permet de stocker les informations suivantes :
 * - extension du fichier
 * - URL de la photo
 * - artisan propriétaire de la photo
 * - dates de création et mise à jour automatiques
 */
@Entity
@Table(name = "uploaded_photos")
public class UploadedPhoto {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /**
     * Identifiant unique de la photo.
     * Généré automatiquement et non modifiable.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /**
     * Extension du fichier (ex : jpg, png).
     */
    @Column(nullable = false)
    private String extension;

    /**
     * URL ou chemin de la photo uploadée.
     */
    @Column
    private String uploadedPhotoUrl;

    /**
     * Artisan propriétaire de la photo.
     * Relation ManyToOne vers Artisan.
     */
    @ManyToOne
    @JoinColumn(name = "artisan_id", nullable = false)
    private Artisan artisan;

    /**
     * Date et heure de création de la photo.
     * Remplie automatiquement lors de l'insertion.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date et heure de la dernière mise à jour de la photo.
     * Mise à jour automatiquement à chaque modification.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public String getUploadedPhotoUrl() { return uploadedPhotoUrl; }
    public void setUploadedPhotoUrl(String uploadedPhotoUrl) { this.uploadedPhotoUrl = uploadedPhotoUrl; }

    public Artisan getArtisan() { return artisan; }
    public void setArtisan(Artisan artisan) { this.artisan = artisan; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
