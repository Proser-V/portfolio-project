package com.atelierlocal.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
public class EventCategory {
    // Atributs

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Size(max = 50, message = "La catégorie ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private List<String> artisanCatagories;

    // Getters et Setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getArtisanCategories() { return artisanCatagories; }
    public void setArtisanCategores(List<String> artisanCategories) { this.artisanCatagories = artisanCategories; }

}
