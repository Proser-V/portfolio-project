package com.atelierlocal.service;

import org.springframework.stereotype.Service;
import com.password4j.Password;

@Service
public class PasswordService {

    public String hashPassword(String rawPassword) {
        return Password.hash(rawPassword)
                    .addRandomSalt()
                    .withArgon2()
                    .getResult();
    }
    
    public boolean verify(String rawPassword, String hashed) {
        return Password.check(rawPassword, hashed).withArgon2();
    }
}
