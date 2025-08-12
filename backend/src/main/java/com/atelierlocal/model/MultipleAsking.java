package com.atelierlocal.model;

import jakarta.persistence.*;
import java.util.UUID;
import java.util.List;

@Entity
public class MultipleAsking {
    // Atributes

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private List<String> eventCategory;

    @Column(nullable = false)
    private List<String> artisanCategory;

    // Getters and setters

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<String> getArtisanCategory() { return artisanCategory; }
    public void setArtisanCategory(List<String> artisanCategory) { this.artisanCategory = artisanCategory; }

    public List<String> getEventCategory() { return eventCategory; }
    public void setEventCategory(List<String> eventCategory) { this.eventCategory = eventCategory; }
}
