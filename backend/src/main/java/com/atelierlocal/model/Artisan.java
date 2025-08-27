package com.atelierlocal.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "artisans")
public class Artisan extends User {
    // Atributes

    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 12)
    @Pattern(regexp = "^(|(\\+33|0)[1-9](\\d{2}){4}$", message = "Numéro invalide (format français attendu)")
    @Size(max = 12)
    private String phoneNumber;

    @Column(name = "hashed_password", nullable = false, length = 255)
    private String hashedPwd;

    @Size(max = 500, message = "La bio ne peut pas dépasser 500 caractères.")
    @Column(length = 500)
    private String bio;

    @OneToOne
    private ArtisanCategory category;

    @Embedded
    private Address address;

    @OneToOne(mappedBy = "artisan", cascade = CascadeType.ALL, orphanRemoval = true)
    private ArtisanAvatar avatar;

    @OneToMany(mappedBy = "creator")
    private List<UploadedEstimation> uploadedFiles;

    // Getters and setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getHashedPassword() { return hashedPwd; }
    public void setHashedPassword(String hashedPwd) { this.hashedPwd = hashedPwd; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public ArtisanCategory getCategory() { return category; }
    public void setCategory(ArtisanCategory category) { this.category = category; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public Avatar getAvatar() { return avatar; }
    public void setAvatar(Avatar avatar) { this.avatar = avatar; }

    public List<UploadedEstimation> getUploadedFile() { return uploadedFiles; }
    public void setUploadedFile(List<UploadedEstimation> uploadedFiles) { this.uploadedFiles = uploadedFiles;}
}
