package com.atelierlocal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "multiple_askings")
public class Asking {
    // Atributs

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Size(max = 1000, message = "La demande ne peux excéder 1000 caractères.")
    @Column(nullable = false, length = 1000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "event_category_id", nullable = true)
    private EventCategory eventCategory;

    @ManyToMany
    @JoinTable(
        name = "asking_artisan_category",
        joinColumns = @JoinColumn(name = "asking_id"),
        inverseJoinColumns = @JoinColumn(name = "artisan_category_id")
    )
    private List<ArtisanCategory> artisanCategoryList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private AskingStatus status;

    // Getters et setters

    public UUID getId() { return id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<ArtisanCategory> getArtisanCategory() { return artisanCategoryList; }
    public void setArtisanCategory(List<ArtisanCategory> artisanCategoryList) { this.artisanCategoryList = artisanCategoryList; }

    public EventCategory getEventCategory() { return eventCategory; }
    public void setEventCategory(EventCategory eventCategory) { this.eventCategory = eventCategory; }

    public AskingStatus getStatus() { return status; }
    public void setStatus(AskingStatus status) { this.status = status; }
}
