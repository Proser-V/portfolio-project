package com.atelierlocal.dto;

import java.util.UUID;

import com.atelierlocal.model.Client;
import com.atelierlocal.model.UserRole;

/**
 * DTO (Data Transfer Object) utilisé pour exposer les informations d'un client
 * via l'API. 
 * 
 * Ce DTO contient les informations essentielles d'un client, y compris
 * sa localisation, son avatar, son rôle et son statut actif.
 */
public class ClientResponseDTO {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /**
     * Identifiant unique du client
     */
    private UUID id;

    /**
     * Adresse email du client
     */
    private String email;

    /**
     * Prénom du client
     */
    private String firstName;

    /**
     * Nom de famille du client
     */
    private String lastName;

    /**
     * Avatar du client (optionnel)
     */
    private AvatarDTO avatar;

    /**
     * Latitude de la localisation du client
     */
    private Double latitude;

    /**
     * Longitude de la localisation du client
     */
    private Double longitude;

    /**
     * Numéro de téléphone du client (optionnel)
     */
    private String phoneNumber;

    /**
     * Nombre de recommandations reçues par le client
     */
    private int recommendationsCount;

    /**
     * Rôle de l'utilisateur (CLIENT, ADMIN, etc.)
     */
    public UserRole role;

    /**
     * Statut actif du client
     */
    private Boolean isActive;

    // -------------------------------------------------------------------------
    // CONSTRUCTEUR
    // -------------------------------------------------------------------------

    /**
     * Constructeur pour créer un DTO à partir d'un objet Client.
     * 
     * @param client l'entité Client à transformer en DTO
     */
    public ClientResponseDTO(Client client) {
        this.id = client.getId();
        this.email = client.getEmail();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.latitude = client.getLatitude();
        this.longitude = client.getLongitude();
        this.avatar = client.getAvatar() != null ? new AvatarDTO(client.getAvatar()) : null;
        this.phoneNumber = client.getPhoneNumber();
        this.recommendationsCount = client.getRecommendations() != null ? client.getRecommendations().size() : 0;
        this.role = client.getUserRole();
        this.isActive = client.getActive();
    }

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public AvatarDTO getAvatar() { return avatar; }
    public void setAvatar(AvatarDTO avatar) { this.avatar = avatar; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; } 

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public int getRecommendationsCount() { return recommendationsCount; }
    public void setRecommendationsCount(int recommendationsCount) { this.recommendationsCount = recommendationsCount; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public Boolean getActive() { return isActive; }
    public void setActive(Boolean isActive) { this.isActive = isActive; }
}
