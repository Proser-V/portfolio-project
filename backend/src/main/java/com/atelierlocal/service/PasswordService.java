package com.atelierlocal.service;

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
}
