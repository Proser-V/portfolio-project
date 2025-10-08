package com.atelierlocal.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

public class UploadedPhotoRequestDTO {
    // Attributs
    @NotNull(message = "Le fichier est obligatoire.")
    private MultipartFile file;

    // Getters et setters
    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }
}
