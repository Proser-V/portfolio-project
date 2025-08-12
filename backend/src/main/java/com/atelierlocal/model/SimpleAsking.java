package com.atelierlocal.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class SimpleAsking {
    // Atributes

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String artisanCategory;

    // Getters and setters

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getArtisanCategory() { return artisanCategory; }
    public void setArtisanCategory(String artisanCategory) { this.artisanCategory = artisanCategory; }
}
