package com.atelierlocal.dto;

import java.util.UUID;

import com.atelierlocal.model.Client;

public class ClientResponseDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private AvatarDTO avatar;
    private AddressDTO address;
    private String phoneNumber;
    private int recommendationsCount;

    public ClientResponseDTO(Client client) {
        this.id = client.getId();
        this.email = client.getEmail();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.address = client.getAddress() != null ? new AddressDTO(client.getAddress()) : null;
        this.avatar = client.getAvatar() != null ? new AvatarDTO(client.getAvatar()) : null;
        this.phoneNumber = client.getPhoneNumber();
        this.recommendationsCount = client.getRecommendations() != null ? client.getRecommendations().size() : 0;
    }

    // Getters et setters

    public UUID getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public AvatarDTO getAvatar() { return avatar; }
    public void setAvatar(AvatarDTO avatar) { this.avatar = avatar; }

    public AddressDTO getAddress() { return address; }
    public void setAddress(AddressDTO address) { this.address = address; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; } 

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public int getRecommendationsCount() { return recommendationsCount; }
    public void setRecommendationsCount(int recommendationsCount) { this.recommendationsCount = recommendationsCount; }
}
