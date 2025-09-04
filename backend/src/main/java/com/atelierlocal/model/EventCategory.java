package com.atelierlocal.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "event_categories")
public class EventCategory {
    // Atributs

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Size(max = 50, message = "La catégorie ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50)
    private String name;

    @ManyToMany
    @JoinTable(
        name = "event_artisan_category",
        joinColumns = @JoinColumn(name = "event_category_id"),
        inverseJoinColumns = @JoinColumn(name = "artisan_category_id")
    )
    private List<ArtisanCategory> artisanCategoryList = new ArrayList<>();

    @OneToMany(mappedBy = "eventCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asking> askingsList = new ArrayList<>();

    // Getters et Setters

    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<ArtisanCategory> getArtisanCategoryList() { return artisanCategoryList; }
    public void setArtisanCategoryList(List<ArtisanCategory> artisanCategoryList) { this.artisanCategoryList = artisanCategoryList; }

    public List<Asking> getAskingsList() { return askingsList; }
    public void setAskingsList(List<Asking> askingsList) { this.askingsList = askingsList; }
}
