package com.atelierlocal.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
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
    @Size(min = 10, max = 12)
    private String phoneNumber;

    @Size(max = 500, message = "La bio ne peut pas dépasser 500 caractères.")
    @Column(length = 500)
    private String bio;

    @OneToOne
    @JoinColumn(name = "artisan_category_name")
    private ArtisanCategory category;

    @Column(length = 14)
    @Pattern(regexp = "\\d+", message = "Le champ ne doit contenir que des chiffres") // Validation du format seulement
    @Size(min = 14, max = 14)
    private String siret;

    @OneToMany(mappedBy = "artisan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UploadedPhoto> photoGallery = new ArrayList<>();

    // Getters and setters

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

    public List<UploadedPhoto> getPhotoGallery() { return photoGallery; }
    public void setPhotoGallery(List<UploadedPhoto> photoGallery) { this.photoGallery = photoGallery; }
}
