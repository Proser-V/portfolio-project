package com.atelierlocal.model;

import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
public class ArtisanCategory {
    // Attributs

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Size(max = 50, message = "La catégorie ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50)
    private String name;

    @Size(max = 200, message = "La description ne peut pas dépasser 200 caractères.")
    @Column(nullable = false, length = 200)
    private String description;

    // Getters et setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setdescription(String description) { this.description = description; }
}
