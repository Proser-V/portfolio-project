package com.atelierlocal.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "uploaded_estimations")
public class UploadedEstimation {
    // Atributes

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String extension;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private Artisan creator;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @Column(name = "opening_key")
    private String key;

    // Getters and setters

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public Artisan getCreator() { return creator; }
    public void setCreator(Artisan creator) { this.creator = creator; }

    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
}
