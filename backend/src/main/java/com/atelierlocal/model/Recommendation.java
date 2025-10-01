package com.atelierlocal.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Recommendation {
    // Attributs

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Artisan artisan;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Getters et setters

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Artisan getArtisan() { return artisan; }
    public void setArtisan(Artisan artisan) { this.artisan = artisan; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
