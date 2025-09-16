package com.atelierlocal.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "clients")
public class Client extends User {
    // Atributs

    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50)
    private String firstName;

    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50)
    private String lastName;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asking> askingsList = new ArrayList<>();

    @OneToMany(mappedBy = "client")
    private List<Recommendation> recommendations = new ArrayList<>();

    // Getters et setters

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public List<Asking> getAsking() { return askingsList; }
    public void setAsking(List<Asking> askingsList) { this.askingsList = askingsList; }
}
