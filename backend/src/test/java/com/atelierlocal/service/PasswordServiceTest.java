package com.atelierlocal.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PasswordServiceTest {

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService();
    }

    // --- Tests hash & verify ---

    @Test
    void testHashAndVerifyPassword() {
        String plain = "MotDePasse123!";
        String hash = passwordService.hashPassword(plain);

        assertNotNull(hash, "Le hash ne doit pas être null");
        assertTrue(passwordService.verifyPassword(hash, plain), "Le mot de passe doit être vérifié avec succès");
        assertFalse(passwordService.verifyPassword(hash, "MauvaisMot"), "Un mot de passe incorrect ne doit pas passer la vérification");
    }

    // --- Tests validatePassword ---

    @Test
    void testValidatePassword_success() {
        String password = "Valid123!";
        assertTrue(passwordService.validatePassword(password), "Mot de passe valide doit passer la validation");
    }

    @Test
    void testValidatePassword_failures() {
        assertFalse(passwordService.validatePassword("short"), "Mot de passe trop court doit échouer");
        assertFalse(passwordService.validatePassword("alllowercase123!"), "Mot de passe sans majuscule doit échouer");
        assertFalse(passwordService.validatePassword("ALLUPPERCASE123!"), "Mot de passe sans minuscule doit échouer");
        assertFalse(passwordService.validatePassword("NoNumbers!"), "Mot de passe sans chiffre doit échouer");
        assertFalse(passwordService.validatePassword("NoSpecial123"), "Mot de passe sans caractère spécial doit échouer");
        assertFalse(passwordService.validatePassword(null), "Mot de passe null doit échouer");
    }

    // --- Tests getPasswordValidationErrors ---

    @Test
    void testGetPasswordValidationErrors_none() {
        String password = "Valid123!";
        List<String> errors = passwordService.getPasswordValidationErrors(password);
        assertTrue(errors.isEmpty(), "Mot de passe valide ne doit produire aucune erreur");
    }

    @Test
    void testGetPasswordValidationErrors_multiple() {
        String password = "short";
        List<String> errors = passwordService.getPasswordValidationErrors(password);
        assertFalse(errors.isEmpty(), "Mot de passe invalide doit produire des erreurs");
        assertTrue(errors.stream().anyMatch(e -> e.contains("8 caractères")), "Erreur de longueur doit être présente");
        assertTrue(errors.stream().anyMatch(e -> e.contains("majuscule")), "Erreur de majuscule doit être présente");
        assertTrue(errors.stream().anyMatch(e -> e.contains("chiffre")), "Erreur de chiffre doit être présente");
        assertTrue(errors.stream().anyMatch(e -> e.contains("caractère spécial")), "Erreur de caractère spécial doit être présente");
    }

    @Test
    void testGetPasswordValidationErrors_longPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 130; i++) sb.append("A");
        String longPassword = sb.toString();

        List<String> errors = passwordService.getPasswordValidationErrors(longPassword);
        assertTrue(errors.stream().anyMatch(e -> e.contains("128 caractères")), "Erreur de longueur max doit être présente");
    }
}
