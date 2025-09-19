package com.atelierlocal.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.atelierlocal.model.Artisan;

public class ArtisanResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String bio;
    private String phoneNumber;
    private String siret;
    private String avatarUrl;
    private AddressDTO address;
    private String categoryName;

    public ArtisanResponseDTO(Artisan artisan) {
        this.id = artisan.getId();
        this.name = artisan.getName();
        this.email = artisan.getEmail();
        this.bio = artisan.getBio();
        this.phoneNumber = artisan.getPhoneNumber();
        this.siret = artisan.getSiret();
        this.avatarUrl = artisan.getAvatarUrl();
        this.address = artisan.getAddress() != null ? new AddressDTO(artisan.getAddress()) : null;
        this.categoryName = artisan.getCategory().getName();
    }

    // Getters et setters

    public UUID getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getActivityStartDate() { return activityStartDate; }
    public void setActivityStartDate(LocalDate activityStartDate) { this.activityStartDate = activityStartDate; }
}
