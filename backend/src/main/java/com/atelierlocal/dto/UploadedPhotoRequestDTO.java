package com.atelierlocal.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

/**
 * DTO utilisé pour l'upload d'une photo par un utilisateur.
 * 
 * Contient :
 * - Le fichier à uploader (file)
 * 
 * Ce DTO est utilisé dans les endpoints d'upload pour valider et transférer
 * la photo côté serveur, tout en garantissant que le fichier n'est pas nul.
 */
public class UploadedPhotoRequestDTO {
    
    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------
    
    @NotNull(message = "Le fichier est obligatoire.")
    private MultipartFile file;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------
    
    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }
}
