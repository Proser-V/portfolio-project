package com.atelierlocal.dto;

import com.atelierlocal.model.Address;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Avatar;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateArtisanRequest {
    private String email;
    private String name;
    private String phoneNumber;
    private String bio;
    private ArtisanCategory category;
    private String siret;
    private Avatar avatar;
    private Address address;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String rawPassword;


    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Avatar getAvatar() { return avatar; }
    public void setAvatar(Avatar avatar) { this.avatar = avatar; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public ArtisanCategory getCategory() { return category; }
    public void setCategory(ArtisanCategory category) { this.category = category; }

    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }

    public String getRawPassword() { return rawPassword; }
    public void setRawPassword(String rawPassword) { this.rawPassword = rawPassword; }
}
