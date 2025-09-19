package com.atelierlocal.dto;

import com.atelierlocal.model.Address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddressDTO {

    @Size(max = 10)
    private String number;

    @NotBlank
    @Size(max = 100)
    private String street;

    @NotBlank
    @Pattern(regexp = "\\d{5}", message = "Le code postal doit contenir 5 chiffres.")
    private String postalCode;

    @NotBlank
    @Size(max = 50)
    private String city;

    public AddressDTO(Address address) {
        this.number = address.getNumber();
        this.street = address.getStreet();
        this.postalCode = address.getPostalCode();
        this.city = address.getCity();
    }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}
