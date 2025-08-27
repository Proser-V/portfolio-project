package com.atelierlocal.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users_avatar")
public class ClientAvatar {
    // Atributes

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 5)
    @Size(max = 5)
    private String extension;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Client user;

    // Getters and setters

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public Client getUser() { return user; }
    public void setUser(Client user) { this.user = user; }
}
