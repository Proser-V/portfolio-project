package com.atelierlocal.dto;

import com.atelierlocal.model.Avatar;

/**
 * DTO (Data Transfer Object) représentant l'avatar d'un utilisateur
 * (client ou artisan) côté API.
 * 
 * Ce DTO permet de transférer uniquement les informations nécessaires
 * pour l'affichage de l'avatar, sans exposer l'entité complète.
 */
public class AvatarDTO {

    /**
     * URL de l'avatar (chemin ou lien vers l'image)
     */
    private String url;

    /**
     * Extension du fichier de l'avatar (ex : jpg, png)
     */
    private String extension;

    /**
     * Constructeur à partir de l'entité Avatar.
     * 
     * Initialise le DTO avec l'URL et l'extension du fichier
     * de l'avatar existant.
     * 
     * @param avatar l'entité Avatar à transformer en DTO
     */
    public AvatarDTO(Avatar avatar) {
        this.url = avatar.getAvatarUrl();
        this.extension = avatar.getExtension();
    }

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }
}
