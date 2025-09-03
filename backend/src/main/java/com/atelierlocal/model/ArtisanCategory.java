package com.atelierlocal.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "artisan_categories")
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

    @OneToOne(mappedBy = "category")
    private List<Artisan> artisanList = new ArrayList<>();

    @ManyToMany(mappedBy = "artisanCatagoryList")
    private List<EventCategory> eventCategories = new ArrayList<>();

    @ManyToMany(mappedBy = "artisanCategoryList")
    private List<Asking> askingsList = new ArrayList<>();

    // Getters et setters

    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setdescription(String description) { this.description = description; }

    public List<Artisan> getArtisanList() { return artisanList; }
    public void setArtisanList(List<Artisan> artisanList) { this.artisanList = artisanList; }

    public List<EventCategory> getEventCategories() { return eventCategories; }
    public void setEventCategories(List<EventCategory> eventCategories) { this.eventCategories = eventCategories;}

    public List<Asking> getAskingsList() { return this.askingsList; }
    public void setAskingsList(List<Asking> askingsList) { this.askingsList = askingsList; }
}
