package com.atelierlocal.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Artisan {
    // Attributes

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "hashed_password", nullable = false)
    private String hashedPwd;

    @Column
    private String category;

    @Embedded
    private Address address;

    @Column
    private String avatar;

    // Getters and setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHashedPassword() { return hashedPwd; }
    public void setHashedPassword(String hashedPwd) { this.hashedPwd = hashedPwd; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
