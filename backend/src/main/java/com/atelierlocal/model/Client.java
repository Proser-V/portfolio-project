package com.atelierlocal.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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

    @Embedded
    private Address address;

    @Column
    private List<Asking> askingsList;

    // Getters et setters

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public List<Asking> getAsking() { return askingsList; }
    public void setAsking(List<Asking> askingsList) { this.askingsList = askingsList; }
}
