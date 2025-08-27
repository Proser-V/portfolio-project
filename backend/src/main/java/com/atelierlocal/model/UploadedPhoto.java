package com.atelierlocal.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "uploaded_photos")
public class UploadedPhoto {
    // Atributes

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String extension;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Client user;

    @ManyToOne
    @JoinColumn(name = "artisan_id")
    private Artisan artisan;

    // Getters and setters

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public Client getUser() { return user; }
    public void setUser(Client user) { this.user = user; }

    public Artisan getArtisan() { return artisan; }
    public void setArtisan(Artisan artisan) { this.artisan = artisan; }
}
