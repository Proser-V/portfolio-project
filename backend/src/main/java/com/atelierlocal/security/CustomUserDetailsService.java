package com.atelierlocal.security;

import com.atelierlocal.model.User;
import com.atelierlocal.repository.UserRepo;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * Service personnalisé pour la gestion des utilisateurs dans le cadre de Spring Security.
 * 
 * Implémente :
 * - {@link UserDetailsService} pour charger les utilisateurs à partir de l'email.
 * - {@link UserDetailsPasswordService} pour mettre à jour le mot de passe d'un utilisateur.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService, UserDetailsPasswordService{
    
    /**
     * Référence au repository utilisateur pour accéder aux données persistées.
     */
    private final UserRepo userRepo;

    /**
     * Constructeur injectant le repository utilisateur.
     * 
     * @param userRepo repository pour les opérations CRUD sur les utilisateurs
     */
    public CustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Charge un utilisateur à partir de son email pour Spring Security.
     * 
     * @param email l'email de l'utilisateur
     * @return un objet {@link UserDetails} utilisé par Spring Security
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé avec cet email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Recherche de l'utilisateur en base par email
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));

        // Construction de l'objet UserDetails avec rôle et état du compte
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getHashedPassword()) // mot de passe hashé
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()))
                .accountLocked(!user.getActive()) // compte verrouillé si l'utilisateur n'est pas actif
                .build();
    }

    /**
     * Met à jour le mot de passe d'un utilisateur.
     * 
     * @param user l'utilisateur Spring Security existant
     * @param newPassword le nouveau mot de passe (hashé)
     * @return un nouvel objet {@link UserDetails} mis à jour
     * @throws UsernameNotFoundException si l'utilisateur n'existe pas en base
     */
    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        // Récupération de l'utilisateur en base
        User dbUser = userRepo.findByEmail(user.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + user.getUsername()));

        // Mise à jour du mot de passe hashé
        dbUser.setHashedPassword(newPassword);
        userRepo.save(dbUser);

        // Reconstruction de l'objet UserDetails avec les informations mises à jour
        return org.springframework.security.core.userdetails.User
                .withUsername(dbUser.getEmail())
                .password(dbUser.getHashedPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + dbUser.getUserRole().name()))
                .accountLocked(!dbUser.getActive())
                .build();
    }
}
