package com.atelierlocal.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    private String number;
    private String street;
    private String postalCode;
    private String city;

    public Address() {}

    public Address(String number, String street, String postalCode, String city) {
        this.number = number;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
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
