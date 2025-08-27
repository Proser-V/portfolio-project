package com.atelierlocal.security;

import com.atelierlocal.model.Client;
import com.atelierlocal.repository.UserRepo;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService, UserDetailsPasswordService{
    
    private final UserRepo userRepo;

    public CustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Client user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getHashedPassword()) // ton password hashé
                .authorities(user.getAdmin() ? "ROLE_ADMIN" : "ROLE_USER")
                .accountLocked(!user.getActive()) // si user non actif, compte verrouillé
                .build();
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        Client dbUser = userRepo.findByEmail(user.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + user.getUsername()));

        dbUser.setHashedPassword(newPassword);
        userRepo.save(dbUser);

        return org.springframework.security.core.userdetails.User
                .withUsername(dbUser.getEmail())
                .password(dbUser.getHashedPassword())
                .authorities(dbUser.getAdmin() ? "ADMIN" : "USER")
                .accountLocked(!dbUser.getActive())
                .build();
    }
}
