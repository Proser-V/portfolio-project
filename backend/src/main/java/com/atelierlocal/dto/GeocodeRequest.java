package com.atelierlocal.dto;

/**
 * DTO utilisé pour effectuer une requête de géocodage.
 * 
 * Ce DTO contient l'adresse à géocoder et est utilisé par l'API pour obtenir
 * les coordonnées géographiques correspondantes (latitude et longitude).
 */
public class GeocodeRequest {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /** Adresse complète à géocoder */
    private String address;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
