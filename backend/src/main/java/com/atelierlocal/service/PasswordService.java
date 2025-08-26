package com.atelierlocal.service;

import org.springframework.stereotype.Service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

@Service
public class PasswordService {

    private final Argon2 argon2 = Argon2Factory.create();

    public String hashPassword(char[] plainPassword) {
        return argon2.hash(2, 65536, 1, plainPassword);
    }

    public boolean verifyPassword(String hash, char[] plainPassword) {
        return argon2.verify(hash, plainPassword);
    }
}
