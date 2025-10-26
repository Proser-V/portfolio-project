package com.atelierlocal.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.ArtisanResponseDTO;
import com.atelierlocal.dto.ClientResponseDTO;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Client;
import com.atelierlocal.repository.UserRepo;
import com.atelierlocal.service.ArtisanService;
import com.atelierlocal.service.ClientService;

/**
 * Contrôleur REST pour la gestion des informations utilisateurs.
 * 
 * Ce contrôleur expose des endpoints permettant :
 * - De récupérer les informations de l'utilisateur actuellement authentifié.
 * 
 * Les endpoints sont sécurisés par rôle et accessibles aux clients, artisans et administrateurs.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    // Services métiers pour la gestion des utilisateurs
    private final ClientService clientService;
    private final ArtisanService artisanService;
    private final UserRepo userRepo;

    /**
     * Constructeur avec injection des dépendances.
     * 
     * @param clientService Service pour gérer les clients
     * @param artisanService Service pour gérer les artisans
     * @param userRepo Repository générique pour l'accès aux utilisateurs
     */
    public UserController(ClientService clientService, ArtisanService artisanService, UserRepo userRepo) {
        this.clientService = clientService;
        this.artisanService = artisanService;
        this.userRepo = userRepo;
    }

    // -------------------------------------------------------------------------
    // RÉCUPÉRER L'UTILISATEUR AUTHENTIFIÉ
    // -------------------------------------------------------------------------
    
    /**
     * Retourne les informations de l'utilisateur actuellement authentifié.
     * 
     * Fonctionne pour les utilisateurs de type Client ou Artisan.
     * L'objet principal (@AuthenticationPrincipal) est injecté par Spring Security.
     * 
     * @param principal Objet représentant l'utilisateur authentifié
     * @return ResponseEntity contenant le rôle et les informations de l'utilisateur,
     *         ou une erreur si le type d'utilisateur est inconnu
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CLIENT', 'ARTISAN', 'ADMIN')")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Object principal) {

        // Si l'utilisateur est un client
        if (principal instanceof Client currentClient) {
            ClientResponseDTO dto = clientService.getClientById(currentClient.getId());
            String role = currentClient.getUserRole().name();
            return ResponseEntity.ok(Map.of(
                "role", role,
                "user", dto
            ));
        }

        // Si l'utilisateur est un artisan
        else if (principal instanceof Artisan currentArtisan) {
            ArtisanResponseDTO dto = artisanService.getArtisanById(currentArtisan.getId());
            return ResponseEntity.ok(Map.of(
                "role", "ARTISAN",
                "user", dto
            ));
        }

        // Si le type d'utilisateur est inconnu
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Type d'utilisateur inconnu"));
        }
    }
}
