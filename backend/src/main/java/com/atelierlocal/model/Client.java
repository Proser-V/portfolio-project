package com.atelierlocal.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class Client extends User {
    // Atributes

    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50)
    private String firstName;

    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères.")
    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(name = "hashed_password", nullable = false)
    private String hashedPwd;

    @Embedded
    private Address address;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientAvatar avatar;

    @Column(nullable = false)
    private Boolean isAdmin;

    @Column(nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "client")
    private List<UploadedEstimation> uploadedFiles;

    @OneToMany(mappedBy = "")
    private List<SimpleAsking> simpleAskings;

    @OneToMany(mappedBy = "")
    private List<MultipleAsking> multipleAskings;

    // Getters and setters

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getHashedPassword() { return hashedPwd; }
    public void setHashedPassword(String hashedPwd) { this.hashedPwd = hashedPwd; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public ClientAvatar getAvatar() { return avatar; }
    public void setAvatar(ClientAvatar avatar) { this.avatar = avatar; }

    public Boolean getAdmin() { return isAdmin; }
    public void setAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }

    public Boolean getActive() { return isActive; }
    public void setActive(Boolean isActive) { this.isActive = isActive; }

    public List<UploadedEstimation> getUploadedFile() { return uploadedFiles; }
    public void setUploadedFile(List<UploadedEstimation> uploadedFiles) { this.uploadedFiles = uploadedFiles;}

    public List<SimpleAsking> getSimpleAskings() { return simpleAskings; }
    public void setSimpleAskings(List<SimpleAsking> simpleAskings) { this.simpleAskings = simpleAskings;}

    public List<MultipleAsking> getMultipleAskings() { return multipleAskings; }
    public void setMultipleAskings(List<MultipleAsking> multipleAskings) { this.multipleAskings = multipleAskings;}
}
