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
import com.atelierlocal.service.ArtisanService;
import com.atelierlocal.service.ClientService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ClientService clientService;
    private final ArtisanService artisanService;

    public UserController(ClientService clientService, ArtisanService artisanService) {
        this.clientService = clientService;
        this.artisanService = artisanService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CLIENT', 'ARTISAN', 'ADMIN')")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Object principal) {
        if (principal instanceof Client currentClient) {
            ClientResponseDTO dto = clientService.getClientById(currentClient.getId());
            String role = currentClient.getUserRole().name();
            return ResponseEntity.ok(Map.of(
                "role", role,
                "user", dto
            ));
        }
        else if (principal instanceof Artisan currentArtisan) {
            ArtisanResponseDTO dto = artisanService.getArtisanById(currentArtisan.getId());
            return ResponseEntity.ok(Map.of(
                "role", "ARTISAN",
                "user", dto
            ));
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Type d'utilisateur inconnu"));
        }
    }
}
