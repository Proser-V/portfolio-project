package com.atelierlocal.dto;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) utilisé pour la création ou la mise à jour
 * d'un artisan via les endpoints REST.
 * 
 * Ce DTO contient toutes les informations nécessaires pour créer ou mettre
 * à jour un artisan, avec des contraintes de validation afin d'assurer
 * l'intégrité des données côté serveur.
 */
public class ArtisanRequestDTO {

    // -------------------------------------------------------------------------
    // ATTRIBUTS PRINCIPAUX
    // -------------------------------------------------------------------------

    /**
     * Email de l'artisan.
     * - Obligatoire
     * - Doit être un email valide
     */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    /**
     * Mot de passe de l'artisan.
     * - Obligatoire
     */
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    /**
     * Nom de l'artisan.
     * - Obligatoire
     * - Longueur maximale : 50 caractères
     */
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères.")
    private String name;

    /**
     * Biographie de l'artisan.
     * - Optionnelle
     * - Longueur maximale : 500 caractères
     */
    @Size(max = 500, message = "La bio ne peut pas dépasser 500 caractères.")
    private String bio;

    /**
     * Nom de la catégorie de l'artisan.
     * - Obligatoire
     */
    @NotBlank(message = "La catégorie est obligatoire.")
    private String categoryName;

    /**
     * Numéro SIRET de l'artisan.
     * - Doit contenir uniquement des chiffres
     * - Exactement 14 chiffres
     */
    @Pattern(regexp = "\\d+", message = "Le SIRET doit contenir uniquement des chiffres")
    @Size(min = 14, max = 14, message = "Le SIRET doit contenir 14 chiffres.")
    private String siret;

    /**
     * Latitude de l'emplacement de l'artisan.
     * - Obligatoire
     * - Doit être comprise entre -90.0 et 90.0
     */
    @NotNull(message = "La latitude est obligatoire")
    @DecimalMin(value = "-90.0", message = "Latitude invalide")
    @DecimalMax(value = "90.0", message = "Latitude invalide")
    private Double latitude;

    /**
     * Longitude de l'emplacement de l'artisan.
     * - Obligatoire
     * - Doit être comprise entre -180.0 et 180.0
     */
    @NotNull(message = "La longitude est obligatoire")
    @DecimalMin(value = "-180.0", message = "Longitude invalide")
    @DecimalMax(value = "180.0", message = "Longitude invalide")
    private Double longitude;

    /**
     * Numéro de téléphone de l'artisan.
     * - Optionnel
     * - Doit respecter le format français
     */
    @Pattern(regexp = "(|(\\+33|0)[1-9](\\d{2}){4})$", message = "Numéro invalide (format français requis)")
    private String phoneNumber;

    /**
     * Date de début d'activité de l'artisan.
     * - Optionnelle
     */
    private LocalDate activityStartDate;

    /**
     * Avatar de l'artisan.
     * - Optionnel
     * - Ignoré lors de la sérialisation JSON pour ne pas exposer le fichier directement
     */
    @JsonIgnore
    private MultipartFile avatar;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public LocalDate getActivityStartDate() { return activityStartDate; }
    public void setActivityStartDate(LocalDate activityStartDate) { this.activityStartDate = activityStartDate; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public MultipartFile getAvatar() { return avatar; }
    public void setAvatar(MultipartFile avatar) { this.avatar = avatar; }
}
