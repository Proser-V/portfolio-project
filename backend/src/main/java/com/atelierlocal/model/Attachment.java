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

/**
 * Entité représentant une pièce jointe attachée à un message.
 * 
 * Cette classe stocke les informations relatives à un fichier envoyé dans un message :
 * - identifiant unique
 * - fichier associé à un message
 * - URL du fichier
 * - type du fichier
 * - dates de création et de mise à jour automatiques
 */
@Entity
public class Attachment {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /**
     * Identifiant unique de la pièce jointe.
     * Généré automatiquement et non modifiable.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Message auquel la pièce jointe est associée.
     * Relation ManyToOne vers Message (obligatoire).
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    /**
     * URL ou chemin du fichier stocké.
     */
    @Column(nullable = false)
    private String fileUrl;

    /**
     * Type du fichier (ex : image/png, application/pdf, etc.).
     */
    @Column(nullable = false)
    private String fileType;

    /**
     * Date et heure de création de la pièce jointe.
     * Remplie automatiquement lors de l'insertion.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date et heure de la dernière mise à jour de la pièce jointe.
     * Mise à jour automatiquement à chaque modification.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }

    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
