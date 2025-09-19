package com.atelierlocal.dto;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ArtisanRequestDTO {

    // Attributs
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères.")
    private String name;

    @Size(max = 500, message = "La bio ne peut pas dépasser 500 caractères.")
    private String bio;

    @NotBlank(message = "La catégorie est obligatoire.")
    private String categoryName;

    @Pattern(regexp = "\\d+", message = "Le SIRET doit contenir uniquement des chiffres")
    @Size(min = 14, max = 14, message = "Le SIRET doit contenir 14 chiffres.")
    private String siret;

    private AddressDTO address;

    @Pattern(regexp = "(|(\\+33|0)[1-9](\\d{2}){4})$", message = "Numéro invalide (format français requis)")
    private String phoneNumber;

    private LocalDate activityStartDate;

    private MultipartFile avatar;

    // Getters et setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public LocalDate getActivityStartDate() { return activityStartDate; }
    public void setActivityStartDate(LocalDate activityStartDate) { this.activityStartDate = activityStartDate; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public AddressDTO getAddress() { return address; }
    public void setAddress(AddressDTO address) { this.address = address; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public MultipartFile getAvatar() { return avatar; }
    public void setAvatar(MultipartFile avatar) { this.avatar = avatar; }

}