package com.atelierlocal.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    // Attributs
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;


    @Column(nullable = false, unique = true, length = 100)
    @Email(message = "Format d'email invalide")
    @Size(max = 100, message = "L'email ne peux dépasser 100 caractères.")
    private String email;

    // Setter/Getter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UUID getId() { return id; }
}
