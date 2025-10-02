package com.atelierlocal.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.atelierlocal.model.Artisan;


public class ArtisanResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String bio;
    private String phoneNumber;
    private String siret;
    private AvatarDTO avatar;
    private Double latitude;
    private Double longitude;
    private String categoryName;
    private LocalDate activityStartDate;
    private int recommendationsCount;
    private List<UploadedPhotoResponseDTO> photoGallery;

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
        this.categoryName = artisan.getCategory() != null ? artisan.getCategory().getName() : null;
        this.activityStartDate = artisan.getActivityStartDate();
        this.recommendationsCount = artisan.getRecommendations() != null ? artisan.getRecommendations().size() : 0;
        this.photoGallery = artisan.getPhotoGallery().stream()
            .map(UploadedPhotoResponseDTO::new)
            .toList();
    }

    // Getters et setters

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

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public LocalDate getActivityStartDate() { return activityStartDate; }
    public void setActivityStartDate(LocalDate activityStartDate) { this.activityStartDate = activityStartDate; }

    public int getRecommendations() { return recommendationsCount; }
    public void setRecommendations(int recommendationsCount) { this.recommendationsCount = recommendationsCount; }

    public List<UploadedPhotoResponseDTO> getPhotoGallery() { return photoGallery; }
    public void setPhotoGallery(List<UploadedPhotoResponseDTO> photoGallery) { this.photoGallery = photoGallery; }
}
