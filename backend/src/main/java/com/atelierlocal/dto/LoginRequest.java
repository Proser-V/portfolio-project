package com.atelierlocal.dto;

/**
 * DTO utilisé pour la connexion d'un utilisateur.
 * 
 * Ce DTO contient les informations nécessaires pour authentifier un utilisateur :
 * - email : l'adresse email de l'utilisateur
 * - password : le mot de passe associé à l'email
 * 
 * Il est généralement utilisé dans les endpoints de login pour transmettre les
 * identifiants depuis le client vers le serveur.
 */
public class LoginRequest {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /** Email de l'utilisateur */
    private String email;

    /** Mot de passe de l'utilisateur */
    private String password;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password;} 
}
