package com.atelierlocal.security;

import com.atelierlocal.model.User;
import com.atelierlocal.repository.UserRepo;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService {
    
    private final UserRepo userRepo;

    public CustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getHashedPassword()) // ton password hashé
                .authorities(user.getAdmin() ? "ROLE_ADMIN" : "ROLE_USER")
                .accountLocked(!user.getActive()) // si user non actif, compte verrouillé
                .build();
    }
}
