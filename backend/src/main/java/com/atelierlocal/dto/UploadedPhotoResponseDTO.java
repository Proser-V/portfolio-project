package com.atelierlocal.dto;

import java.util.UUID;

import com.atelierlocal.model.UploadedPhoto;

/**
 * DTO de réponse pour une photo uploadée.
 * 
 * Ce DTO est utilisé pour renvoyer les informations d'une photo côté client.
 * Il contient :
 * - l'identifiant de la photo (id)
 * - l'identifiant du propriétaire de la photo (ownerId)
 * - l'URL d'accès à la photo (fileUrl)
 * - l'extension du fichier (fileExtension)
 */
public class UploadedPhotoResponseDTO {
    
    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------
    
    private UUID id;
    private UUID ownerId;
    private String fileUrl;
    private String fileExtension;

    // -------------------------------------------------------------------------
    // CONSTRUCTEUR
    // -------------------------------------------------------------------------
    
    /**
     * Construit un DTO à partir d'une entité UploadedPhoto.
     * 
     * @param photo l'entité UploadedPhoto
     */
    public UploadedPhotoResponseDTO(UploadedPhoto photo) {
        this.id = photo.getId();
        this.ownerId = photo.getArtisan().getId();
        this.fileUrl = photo.getUploadedPhotoUrl();
        this.fileExtension = photo.getExtension();
    }

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------
    
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileExtension() { return fileExtension; }
    public void setFileExtension(String fileExtension) { this.fileExtension = fileExtension; }
}
