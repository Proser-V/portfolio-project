package com.atelierlocal.dto;

import java.util.UUID;

import com.atelierlocal.model.Client;
import com.atelierlocal.model.UserRole;

public class ClientResponseDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private AvatarDTO avatar;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
    private int recommendationsCount;
    public UserRole role;
    private Boolean isActive;

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

    // Getters et setters

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
