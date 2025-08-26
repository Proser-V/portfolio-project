package com.atelierlocal.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users_avatar")
public class ArtisanAvatar {
    // Atributes

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String extension;

    @OneToOne
    @JoinColumn(name = "artisan_id", nullable = false, unique = true)
    private Artisan artisan;

    // Getters and setters

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public Artisan getArtisan() { return artisan; }
    public void setArtisan(Artisan artisan) { this.artisan = artisan; }
}
