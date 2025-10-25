package com.atelierlocal.dto;

import org.springframework.web.multipart.MultipartFile;

import com.atelierlocal.model.UserRole;
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
 * d'un client via l'API.
 * 
 * Ce DTO contient toutes les informations nécessaires pour enregistrer
 * un client, avec validation des champs pour garantir l'intégrité des données.
 */
public class ClientRequestDTO {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /**
     * Adresse email du client (obligatoire et valide)
     */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    /**
     * Mot de passe du client (obligatoire)
     */
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    /**
     * Prénom du client (obligatoire, max 50 caractères)
     */
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères.")
    private String firstName;

    /**
     * Nom de famille du client (obligatoire, max 50 caractères)
     */
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères.")
    private String lastName;

    /**
     * Latitude de la localisation du client (obligatoire, valeur valide entre -90 et 90)
     */
    @NotNull(message = "La latitude est obligatoire")
    @DecimalMin(value = "-90.0", message = "Latitude invalide")
    @DecimalMax(value = "90.0", message = "Latitude invalide")
    private Double latitude;

    /**
     * Longitude de la localisation du client (obligatoire, valeur valide entre -180 et 180)
     */
    @NotNull(message = "La longitude est obligatoire")
    @DecimalMin(value = "-180.0", message = "Longitude invalide")
    @DecimalMax(value = "180.0", message = "Longitude invalide")
    private Double longitude;

    /**
     * Numéro de téléphone du client (optionnel, format français)
     */
    @Pattern(regexp = "(|(\\+33|0)[1-9](\\d{2}){4})$", message = "Numéro invalide (format français requis)")
    private String phoneNumber;

    /**
     * Fichier avatar du client (optionnel, ignoré dans la sérialisation JSON)
     */
    @JsonIgnore
    private MultipartFile avatar;

    /**
     * Rôle de l'utilisateur (CLIENT par défaut)
     */
    private UserRole userRole;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public MultipartFile getAvatar() { return avatar; }
    public void setAvatar(MultipartFile avatar) { this.avatar = avatar; }

    public UserRole getRole() { return userRole; }
    public void setRole(UserRole userRole) { this.userRole = userRole; }
}
