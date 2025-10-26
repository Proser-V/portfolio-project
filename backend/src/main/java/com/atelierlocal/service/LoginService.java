package com.atelierlocal.service;

import org.springframework.stereotype.Service;

import com.atelierlocal.model.User;
import com.atelierlocal.repository.UserRepo;

/**
 * Service pour gérer l'authentification des utilisateurs.
 * 
 * Fournit une méthode de login qui vérifie :
 * - que l'email existe dans la base,
 * - que l'utilisateur est actif,
 * - que le mot de passe fourni correspond au mot de passe haché.
 */
@Service
public class LoginService {

    private final PasswordService passwordService;
    private final UserRepo userRepo;

    /**
     * Constructeur du service.
     * 
     * @param userRepo repository pour accéder aux utilisateurs
     * @param passwordService service pour hacher et vérifier les mots de passe
     */
    public LoginService(UserRepo userRepo, PasswordService passwordService) {
        this.userRepo = userRepo;
        this.passwordService = passwordService;
    }

    /**
     * Tente de connecter un utilisateur avec son email et mot de passe.
     * 
     * @param email email de l'utilisateur
     * @param rawPassword mot de passe en clair fourni par l'utilisateur
     * @return true si les informations sont correctes et l'utilisateur est actif, false sinon
     */
    public boolean login(String email, String rawPassword) {
        try {
            return userRepo.findByEmail(email)        // Recherche l'utilisateur par email
                .filter(User::getActive)             // Vérifie que l'utilisateur est actif
                .map(user -> passwordService.verifyPassword(user.getHashedPassword(), rawPassword)) // Vérifie le mot de passe
                .orElse(false);                       // Retourne false si utilisateur non trouvé ou inactif
        } catch (Exception e) {
            System.err.println("Erreur de la tentative de login: " + e.getMessage());
            return false;                              // En cas d'erreur inattendue, retourne false
        }
    }
}
