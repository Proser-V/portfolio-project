package com.atelierlocal.model;

import jakarta.persistence.*;
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

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "hashed_password", nullable = false)
    private String hashedPwd;

    @Embedded
    private Address address;

    @Column
    private String avatar;

    @Column(nullable = false)
    private Boolean isAdmin;

    @Column(nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "client")
    private List<UploadedFile> uploadedFiles;

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

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Boolean getAdmin() { return isAdmin; }
    public void setAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }

    public Boolean getActive() { return isActive; }
    public void setActive(Boolean isActive) { this.isActive = isActive; }

    public List<UploadedFile> getUploadedFile() { return uploadedFiles; }
    public void setUploadedFile(List<UploadedFile> uploadedFiles) { this.uploadedFiles = uploadedFiles;}
}
