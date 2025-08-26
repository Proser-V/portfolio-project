package com.atelierlocal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.UUID;

import com.atelierlocal.controller.ArtisanController;

@Entity
public class SimpleAsking {
    // Atributes

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Size(max = 1000, message = "La demande ne peux excéder 1000 caractères.")
    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private ArtisanCategory artisanCategory;

    // Getters and setters

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public ArtisanCategory getArtisanCategory() { return artisanCategory; }
    public void setArtisanCategory(ArtisanCategory artisanCategory) { this.artisanCategory = artisanCategory; }
}
