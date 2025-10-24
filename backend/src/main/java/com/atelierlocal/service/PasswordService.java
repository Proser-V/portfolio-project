package com.atelierlocal.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;


@Service
public class PasswordService {

    private final Argon2 argon2 = Argon2Factory.create();

    public String hashPassword(String plainPassword) {
        char [] passwordArray = plainPassword.toCharArray();
        try {
            return argon2.hash(2, 65536, 1, passwordArray);
        } finally {
            java.util.Arrays.fill(passwordArray, '\0');
        }
    }

    public boolean verifyPassword(String hash, String plainPassword) {
        char[] passwordArray = plainPassword.toCharArray();
        try {
            return argon2.verify(hash, passwordArray);
        } finally {
            java.util.Arrays.fill(passwordArray, '\0');
        }
    }

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

    public List<String> getPasswordValidationErrors(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.length() < 8) {
            errors.add("Le mot de passe doit contenir au moins 8 caractères");
        }
        if (password != null && password.length() > 128) {
            errors.add("Le mot de passe ne peut pas dépasser 128 caractères");
        }
        if (password != null) {
            boolean hasUpperCase = false;
            boolean hasLowerCase = false;
            boolean hasDigit = false;
            boolean hasSpecialChar = false;

            String specialChars = "!@#$%^&*(),.?\":{}|<>";

            for (char c : password.toCharArray()) {
                if (Character.isUpperCase(c)) hasUpperCase = true;
                else if (Character.isLowerCase(c)) hasLowerCase = true;
                else if (Character.isDigit(c)) hasDigit = true;
                else if (specialChars.indexOf(c) != -1) hasSpecialChar = true;
            }

            if (!hasUpperCase) errors.add("Il manque au moins une majuscule (A-Z).");
            if (!hasLowerCase) errors.add("Il manque au moins une minuscule (a-z).");
            if (!hasDigit) errors.add("Il manque au moins un chiffre (0-9).");
            if (!hasSpecialChar) errors.add("Il manque au moins un caractère spécial (ex: !@#$%^&*).");
        }

        return errors;
    }
}
