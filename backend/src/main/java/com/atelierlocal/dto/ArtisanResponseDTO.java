package com.atelierlocal.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.UserRole;

/**
 * DTO (Data Transfer Object) utilisé pour renvoyer les informations détaillées
 * d'un artisan via les endpoints REST.
 * 
 * Ce DTO contient toutes les données visibles côté client, y compris :
 * - les informations personnelles et professionnelles
 * - les coordonnées géographiques
 * - les informations de catégorie et d'activité
 * - le nombre de recommandations et la galerie de photos
 * - le rôle de l'utilisateur
 */
public class ArtisanResponseDTO {

    /**
     * Identifiant unique de l'artisan
     */
    private UUID id;

    /**
     * Nom de l'artisan
     */
    private String name;

    /**
     * Email de l'artisan
     */
    private String email;

    /**
     * Biographie ou description de l'artisan
     */
    private String bio;

    /**
     * Numéro de téléphone de l'artisan
     */
    private String phoneNumber;

    /**
     * Numéro SIRET de l'artisan
     */
    private String siret;

    /**
     * Avatar de l'artisan (photo de profil)
     */
    private AvatarDTO avatar;

    /**
     * Latitude de l'emplacement de l'artisan
     */
    private Double latitude;

    /**
     * Longitude de l'emplacement de l'artisan
     */
    private Double longitude;

    /**
     * Identifiant de la catégorie de l'artisan
     */
    private UUID categoryId;

    /**
     * Nom de la catégorie de l'artisan
     */
    private String categoryName;

    /**
     * Date de début d'activité de l'artisan
     */
    private LocalDate activityStartDate;

    /**
     * Nombre de recommandations reçues par l'artisan
     */
    private int recommendationsCount;

    /**
     * Galerie de photos téléchargées par l'artisan
     */
    private List<UploadedPhotoResponseDTO> photoGallery;

    /**
     * Rôle de l'utilisateur (ARTISAN)
     */
    private UserRole role;

    /**
     * Constructeur à partir d'un objet Artisan.
     * 
     * Ce constructeur initialise tous les champs du DTO en extrayant les
     * informations pertinentes de l'entité Artisan, y compris :
     * - avatar transformé en DTO
     * - catégorie et ID
     * - galerie de photos transformée en DTOs
     * - nombre de recommandations
     * - rôle
     * 
     * @param artisan l'entité Artisan à transformer en DTO
     */
    public ArtisanResponseDTO(Artisan artisan) {
        this.id = artisan.getId();
        this.name = artisan.getName();
        this.email = artisan.getEmail();
        this.bio = artisan.getBio();
        this.phoneNumber = artisan.getPhoneNumber();
        this.siret = artisan.getSiret();
        this.avatar = artisan.getAvatar() != null ? new AvatarDTO(artisan.getAvatar()) : null;
        this.latitude = artisan.getLatitude();
        this.longitude = artisan.getLongitude();
        this.categoryId = artisan.getCategory() != null ? artisan.getCategory().getId() : null;
        this.categoryName = artisan.getCategory() != null ? artisan.getCategory().getName() : null;
        this.activityStartDate = artisan.getActivityStartDate();
        this.recommendationsCount = artisan.getRecommendations() != null ? artisan.getRecommendations().size() : 0;
        this.photoGallery = artisan.getPhotoGallery().stream()
            .map(UploadedPhotoResponseDTO::new)
            .toList();
        this.role = artisan.getUserRole();
    }

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }

    public AvatarDTO getAvatar() { return avatar; }
    public void setAvatar(AvatarDTO avatar) { this.avatar = avatar; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public LocalDate getActivityStartDate() { return activityStartDate; }
    public void setActivityStartDate(LocalDate activityStartDate) { this.activityStartDate = activityStartDate; }

    public int getRecommendations() { return recommendationsCount; }
    public void setRecommendations(int recommendationsCount) { this.recommendationsCount = recommendationsCount; }

    public List<UploadedPhotoResponseDTO> getPhotoGallery() { return photoGallery; }
    public void setPhotoGallery(List<UploadedPhotoResponseDTO> photoGallery) { this.photoGallery = photoGallery; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
