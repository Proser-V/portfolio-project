package com.atelierlocal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "artisans")
public class Artisan {
    // Atributes

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    @Email(message = "Format d'email invalide")
    @Size(max = 100, message = "L'email ne peux dépasser 100 caractères.")
    private String email;

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
    private List<UploadedFile> uploadedFiles;

    // Getters and setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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

    public List<UploadedFile> getUploadedFile() { return uploadedFiles; }
    public void setUploadedFile(List<UploadedFile> uploadedFiles) { this.uploadedFiles = uploadedFiles;}
}
