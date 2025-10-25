package com.atelierlocal.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Entité abstraite représentant un utilisateur.
 * 
 * Cette classe est la super-classe pour tous les types d'utilisateurs (Artisan, Client).
 * Elle gère les informations communes :
 * - email et mot de passe hashé
 * - activation du compte
 * - rôle utilisateur
 * - avatar
 * - coordonnées géographiques
 * - numéro de téléphone
 * - dates de création et mise à jour automatiques
 * 
 * Implémente UserDetails pour l'intégration avec Spring Security.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User implements UserDetails {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /**
     * Identifiant unique de l'utilisateur.
     * Généré automatiquement et non modifiable.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Email de l'utilisateur, unique et obligatoire.
     */
    @Column(nullable = false, unique = true, length = 100)
    @Email(message = "Format d'email invalide.")
    @Size(max = 100, message = "L'email ne peut dépasser 100 caractères.")
    private String email;

    /**
     * Mot de passe hashé de l'utilisateur.
     */
    @Column(name = "hashed_password", nullable = false)
    private String hashedPwd;

    /**
     * Indique si le compte est actif.
     */
    @Column(nullable = false)
    private Boolean isActive;

    /**
     * Rôle de l'utilisateur (enum UserRole).
     */
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    /**
     * Avatar de l'utilisateur.
     * Relation OneToOne, cascade sur toutes les opérations.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Avatar avatar;

    /**
     * Latitude de l'utilisateur (optionnelle).
     */
    @Column
    @DecimalMin(value = "-90.0", message = "La latitude doit être supérieure ou égale à -90.")
    @DecimalMax(value = "90.0", message = "La latitude doit être inférieure ou égale à 90.")
    private Double latitude;

    /**
     * Longitude de l'utilisateur (optionnelle).
     */
    @Column
    @DecimalMin(value = "-180.0", message = "La longitude doit être supérieure ou égale à -180.")
    @DecimalMax(value = "180.0", message = "La longitude doit être inférieure ou égale à 180.")
    private Double longitude;

    /**
     * Numéro de téléphone de l'utilisateur (format français attendu).
     */
    @Column(length = 12)
    @Pattern(regexp = "^(|(\\+33|0)[1-9](\\d{2}){4})$", message = "Numéro invalide (format français attendu)")
    @Size(min = 10, max = 12)
    private String phoneNumber;

    /**
     * Date et heure de création du compte.
     * Remplie automatiquement lors de l'insertion.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date et heure de la dernière mise à jour du compte.
     * Mise à jour automatiquement à chaque modification.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

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

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // -------------------------------------------------------------------------
    // MÉTHODES USERDETAILS POUR SPRING SECURITY
    // -------------------------------------------------------------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Simple rôle unique basé sur l'enum UserRole
        return Collections.singleton(() -> "ROLE_" + userRole.name());
    }

    @Override
    public String getPassword() {
        return hashedPwd;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Pas de gestion d'expiration pour l'instant
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Pas de verrouillage pour l'instant
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Pas de gestion d'expiration des credentials
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(isActive);
    }
}
