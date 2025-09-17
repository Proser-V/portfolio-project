package com.atelierlocal.dto;

import java.time.LocalDate;
import java.util.UUID;

public class ArtisanDto {
    private UUID id;
    private String email;
    private String avatarUrl;
    private String name;
    private LocalDate activityStartDate;

    public ArtisanDto(UUID id, String email, String avatarUrl, String name, LocalDate activityStartDate) {
        this.id = id;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.activityStartDate = activityStartDate;
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
