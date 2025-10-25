package com.atelierlocal.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/**
 * Service de gestion des mots de passe.
 * 
 * Fournit des fonctionnalités pour :
 * - le hachage sécurisé des mots de passe avec Argon2,
 * - la vérification d'un mot de passe contre son hash,
 * - la validation des règles de sécurité d'un mot de passe,
 * - la récupération des erreurs de validation détaillées.
 */
@Service
public class PasswordService {

    // Instance de Argon2 pour le hachage sécurisé
    private final Argon2 argon2 = Argon2Factory.create();

    /**
     * Hache un mot de passe en clair avec Argon2.
     *
     * @param plainPassword mot de passe en clair
     * @return hash sécurisé du mot de passe
     */
    public String hashPassword(String plainPassword) {
        char[] passwordArray = plainPassword.toCharArray();
        try {
            // Paramètres : itérations = 2, mémoire = 65536 KB, threads = 1
            return argon2.hash(2, 65536, 1, passwordArray);
        } finally {
            // Effacement de la mémoire contenant le mot de passe
            java.util.Arrays.fill(passwordArray, '\0');
        }
    }

    /**
     * Vérifie qu'un mot de passe correspond au hash stocké.
     *
     * @param hash hash sécurisé du mot de passe
     * @param plainPassword mot de passe en clair à vérifier
     * @return true si le mot de passe correspond au hash, false sinon
     */
    public boolean verifyPassword(String hash, String plainPassword) {
        char[] passwordArray = plainPassword.toCharArray();
        try {
            return argon2.verify(hash, passwordArray);
        } finally {
            // Effacement de la mémoire contenant le mot de passe
            java.util.Arrays.fill(passwordArray, '\0');
        }
    }

    /**
     * Valide qu'un mot de passe respecte les règles de sécurité :
     * - longueur entre 8 et 128 caractères,
     * - au moins une majuscule,
     * - au moins une minuscule,
     * - au moins un chiffre,
     * - au moins un caractère spécial.
     *
     * @param password mot de passe à valider
     * @return true si toutes les règles sont respectées, false sinon
     */
    public boolean validatePassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 128) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        String specialChars = "!@#$%^&*(),.?\":{}|<>";

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (specialChars.indexOf(c) != -1) {
                hasSpecialChar = true;
            }
        }
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    /**
     * Fournit la liste détaillée des erreurs de validation d'un mot de passe.
     * 
     * Cette méthode est utile pour informer l'utilisateur précisément de ce qui
     * ne respecte pas les règles de sécurité.
     *
     * @param password mot de passe à vérifier
     * @return liste des messages d'erreur (vide si le mot de passe est valide)
     */
    public List<String> getPasswordValidationErrors(String password) {
        List<String> errors = new ArrayList<>();

        // Vérification de la longueur minimale
        if (password == null || password.length() < 8) {
            errors.add("Le mot de passe doit contenir au moins 8 caractères");
        }

        // Vérification de la longueur maximale
        if (password != null && password.length() > 128) {
            errors.add("Le mot de passe ne peut pas dépasser 128 caractères");
        }

        if (password != null) {
            boolean hasUpperCase = false;
            boolean hasLowerCase = false;
            boolean hasDigit = false;
            boolean hasSpecialChar = false;

            String specialChars = "!@#$%^&*(),.?\":{}|<>";

            // Parcours des caractères pour vérifier les différentes règles
            for (char c : password.toCharArray()) {
                if (Character.isUpperCase(c)) hasUpperCase = true;
                else if (Character.isLowerCase(c)) hasLowerCase = true;
                else if (Character.isDigit(c)) hasDigit = true;
                else if (specialChars.indexOf(c) != -1) hasSpecialChar = true;
            }

            // Ajout des erreurs correspondantes si une règle n'est pas respectée
            if (!hasUpperCase) errors.add("Il manque au moins une majuscule (A-Z).");
            if (!hasLowerCase) errors.add("Il manque au moins une minuscule (a-z).");
            if (!hasDigit) errors.add("Il manque au moins un chiffre (0-9).");
            if (!hasSpecialChar) errors.add("Il manque au moins un caractère spécial (ex: !@#$%^&*).");
        }

        return errors;
    }
}
