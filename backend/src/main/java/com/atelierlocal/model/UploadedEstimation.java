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
    private Client client;

    @Column(name = "opening_key")
    private String key;

    // Getters and setters

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public Artisan getCreator() { return creator; }
    public void setCreator(Artisan creator) { this.creator = creator; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
}
