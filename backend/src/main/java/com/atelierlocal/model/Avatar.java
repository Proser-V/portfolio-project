package com.atelierlocal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "users_avatar")
public class Avatar {
    // Atributes

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 5)
    @Size(max = 5)
    private String extension;

    @OneToOne
    @JoinColumn(name = "user", nullable = false, unique = true)
    private User user;

    @Column
    private String url;

    // Getters and setters

    public UUID getId() { return id; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
