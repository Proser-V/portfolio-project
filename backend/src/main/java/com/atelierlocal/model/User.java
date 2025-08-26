package com.atelierlocal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    // Atributes

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50)
    private String firstName;

    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    @Email(message = "Format d'email invalide")
    @Size(max = 100, message = "L'email ne peux dépasser 100 caractères.")
    private String email;

    @Column(name = "hashed_password", nullable = false)
    private String hashedPwd;

    @Embedded
    private Address address;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserAvatar avatar;

    @Column(nullable = false)
    private Boolean isAdmin;

    @Column(nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "client")
    private List<UploadedFile> uploadedFiles;

    @OneToMany(mappedBy = "")
    private List<SimpleAsking> simpleAskings;

    @OneToMany(mappedBy = "")
    private List<MultipleAsking> multipleAskings;

    // Getters and setters

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHashedPassword() { return hashedPwd; }
    public void setHashedPassword(String hashedPwd) { this.hashedPwd = hashedPwd; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public UserAvatar getAvatar() { return avatar; }
    public void setAvatar(UserAvatar avatar) { this.avatar = avatar; }

    public Boolean getAdmin() { return isAdmin; }
    public void setAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }

    public Boolean getActive() { return isActive; }
    public void setActive(Boolean isActive) { this.isActive = isActive; }

    public List<UploadedFile> getUploadedFile() { return uploadedFiles; }
    public void setUploadedFile(List<UploadedFile> uploadedFiles) { this.uploadedFiles = uploadedFiles;}

    public List<SimpleAsking> getSimpleAskings() { return simpleAskings; }
    public void setSimpleAskings(List<SimpleAsking> simpleAskings) { this.simpleAskings = simpleAskings;}

    public List<MultipleAsking> getMultipleAskings() { return multipleAskings; }
    public void setMultipleAskings(List<MultipleAsking> multipleAskings) { this.multipleAskings = multipleAskings;}
}
