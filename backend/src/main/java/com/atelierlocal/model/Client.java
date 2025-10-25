package com.atelierlocal.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant un client.
 * 
 * Cette classe hérite de User et ajoute des informations spécifiques aux clients :
 * - prénom et nom
 * - liste des demandes (askings)
 * - recommandations laissées
 */
@Entity
@Table(name = "clients")
public class Client extends User {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /**
     * Prénom du client (max 50 caractères).
     */
    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50)
    private String firstName;

    /**
     * Nom du client (max 50 caractères).
     */
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50)
    private String lastName;

    /**
     * Liste des demandes (askings) faites par le client.
     * Relation OneToMany vers Asking, cascade sur toutes les opérations.
     * Ignorée lors de la sérialisation JSON pour éviter les boucles.
     */
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Asking> askingsList = new ArrayList<>();

    /**
     * Liste des recommandations laissées par le client.
     * Relation OneToMany vers Recommendation, cascade sur toutes les opérations.
     * Ignorée lors de la sérialisation JSON pour éviter les boucles.
     */
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Recommendation> recommendations = new ArrayList<>();

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public List<Asking> getAsking() { return askingsList; }
    public void setAsking(List<Asking> askingsList) { this.askingsList = askingsList; }

    public List<Recommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<Recommendation> recommendations) { this.recommendations = recommendations; }
}
