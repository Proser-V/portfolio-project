package com.atelierlocal.dto;

import org.springframework.web.multipart.MultipartFile;

import com.atelierlocal.model.Address;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateClientRequest {
    private String email;
    private String firstName;
    private String lastName;
    private MultipartFile avatar;
    private Address address;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String rawPassword;
    private String phoneNumber;


    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public MultipartFile getAvatar() { return avatar; }
    public void setAvatar(MultipartFile avatar) { this.avatar = avatar; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRawPassword() { return rawPassword; }
    public void setRawPassword(String rawPassword) { this.rawPassword = rawPassword; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
