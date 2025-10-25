package com.atelierlocal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entité représentant l'avatar d'un utilisateur.
 * 
 * Cette classe stocke les informations relatives à l'image de profil d'un utilisateur :
 * - identifiant unique
 * - extension du fichier image
 * - utilisateur associé
 * - URL du fichier
 * - dates de création et de mise à jour automatiques
 */
@Entity
@Table(name = "users_avatar")
public class Avatar {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /**
     * Identifiant unique de l'avatar.
     * Généré automatiquement et non modifiable.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /**
     * Extension du fichier image (ex : jpg, png).
     * Maximum 5 caractères.
     */
    @Column(nullable = false, length = 5)
    @Size(max = 5)
    private String extension;

    /**
     * Utilisateur associé à cet avatar.
     * Relation OneToOne vers User, obligatoire et unique.
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * URL ou chemin du fichier avatar.
     */
    @Column
    private String avatarUrl;

    /**
     * Date et heure de création de l'avatar.
     * Remplie automatiquement lors de l'insertion.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date et heure de la dernière mise à jour de l'avatar.
     * Mise à jour automatiquement à chaque modification.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
