package com.atelierlocal.dto;

import java.util.UUID;

import com.atelierlocal.model.UploadedPhoto;

public class UploadedPhotoResponseDTO {
    private UUID id;
    private UUID ownerId;
    private String fileUrl;
    private String fileExtension;

    public UploadedPhotoResponseDTO(UploadedPhoto photo) {
        this.id = photo.getId();
        this.ownerId = photo.getArtisan().getId();
        this.fileUrl = photo.getUploadedPhotoUrl();
        this.fileExtension = photo.getExtension();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileExtension() { return fileExtension; }
    public void setFileExtension(String fileExtension) { this.fileExtension = fileExtension; }
}
