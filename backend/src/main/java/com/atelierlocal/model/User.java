package com.atelierlocal.model;

import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users") 
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

    @Column(name = "hashed_password", nullable = false)
    private String hashedPwd;

    @Column(nullable = false)
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Avatar avatar;

    @Embedded
    private Address address;

    // Getters et setters

    public UUID getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHashedPassword() { return hashedPwd; }
    public void setHashedPassword(String hashedPwd) { this.hashedPwd = hashedPwd; }

    public Boolean getActive() { return isActive; }
    public void setActive(Boolean isActive) { this.isActive = isActive; }

    public UserRole getUserRole() { return userRole; }
    public void setUserRole(UserRole userRole) { this.userRole = userRole; }

    public Avatar getAvatar() { return avatar; }
    public void setAvatar(Avatar avatar) { this.avatar = avatar; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}
