package com.atelierlocal.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @Size(max = 200, message = "La description ne peut pas dépasser 200 caractères.")
    @Column(nullable = false, length = 200)
    private String description;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Artisan> artisanList = new ArrayList<>();

    @ManyToMany(mappedBy = "artisanCategoryList")
    private List<EventCategory> eventCategories = new ArrayList<>();

    @OneToMany(mappedBy = "artisanCategory")
    private List<Asking> askingsList = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Getters et setters

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Artisan> getArtisanList() { return artisanList; }
    public void setArtisanList(List<Artisan> artisanList) { this.artisanList = artisanList; }

    public List<EventCategory> getEventCategories() { return eventCategories; }
    public void setEventCategories(List<EventCategory> eventCategories) { this.eventCategories = eventCategories;}

    public List<Asking> getAskingsList() { return this.askingsList; }
    public void setAskingsList(List<Asking> askingsList) { this.askingsList = askingsList; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
