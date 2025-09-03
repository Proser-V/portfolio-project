package com.atelierlocal.service;

import com.atelierlocal.model.User;
import com.atelierlocal.repository.UserRepo;

public class LoginService {

    private final PasswordService passwordService;
    private final UserRepo userRepo;

    public LoginService(UserRepo userRepo, PasswordService passwordService) {
        this.userRepo = userRepo;
        this.passwordService = passwordService;
    }

    public boolean login(String email, String rawPassword) {
        try {
            return userRepo.findByEmail(email)
                .filter(User::getActive)
                .map(user -> passwordService.verifyPassword(user.getHashedPassword(), rawPassword))
                .orElse(false);
        } catch (Exception e) {
            System.err.println("Erreur de la tentative de login: " + e.getMessage());
            return false;
        }
    }
}
