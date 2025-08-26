package com.atelierlocal.service;

import org.springframework.stereotype.Service;

import com.atelierlocal.repository.UserRepo;
import com.atelierlocal.model.User;
import com.atelierlocal.model.Address;

@Service
public class UserService {

    private final PasswordService passwordService;
    private final UserRepo userRepo;
    
    public UserService(PasswordService passwordService, UserRepo userRepo) {
        this.passwordService = passwordService;
        this.userRepo = userRepo;
    }

    public User createUser(String firstName, String lastName, String email, Address address, String rawPassword, String avatar, Boolean isActive, Boolean isAdmin) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setAddress(address);
        user.setAvatar(avatar);

        String hashed = passwordService.hashPassword(rawPassword);
        user.setHashedPassword(hashed);
        user.setAdmin(false);
        user.setActive(true);

        return userRepo.save(user);
    }

    public User createAdmin(String firstName, String lastName, String email, Address address, String rawPassword, String avatar, Boolean isActive, Boolean isAdmin) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setAddress(address);
        user.setAvatar(avatar);

        String hashed = passwordService.hashPassword(rawPassword);
        user.setHashedPassword(hashed);
        user.setAdmin(true);
        user.setActive(true);

        return userRepo.save(user);
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
