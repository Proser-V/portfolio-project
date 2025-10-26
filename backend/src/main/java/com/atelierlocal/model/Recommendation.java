package com.atelierlocal.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Entité représentant une recommandation laissée par un client à un artisan.
 * 
 * Cette classe permet de stocker les informations suivantes :
 * - client ayant fait la recommandation
 * - artisan recommandé
 * - date de création de la recommandation
 */
@Entity
public class Recommendation {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /**
     * Identifiant unique de la recommandation.
     * Généré automatiquement.
     */
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Client ayant laissé la recommandation.
     * Relation ManyToOne vers Client, chargement paresseux.
     * Ignoré lors de la sérialisation JSON pour éviter les boucles.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore 
    private Client client;

    /**
     * Artisan recommandé.
     * Relation ManyToOne vers Artisan, chargement paresseux.
     * Ignoré lors de la sérialisation JSON pour éviter les boucles.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artisan_id", nullable = false)
    @JsonIgnore
    private Artisan artisan;

    /**
     * Date et heure de création de la recommandation.
     * Remplie automatiquement lors de l'insertion.
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Artisan getArtisan() { return artisan; }
    public void setArtisan(Artisan artisan) { this.artisan = artisan; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
