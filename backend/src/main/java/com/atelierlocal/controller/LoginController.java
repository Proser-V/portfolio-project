package com.atelierlocal.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.LoginRequest;
import com.atelierlocal.security.CustomUserDetailsService;
import com.atelierlocal.security.JwtService;
import com.atelierlocal.service.LoginService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Login", description = "API pour l'authentification des utilisateurs")
public class LoginController {
    private final LoginService loginService;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public LoginController(LoginService loginService, JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.loginService = loginService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion d'un utilisateur", description = "Génère un JWT en cas de succès")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connexion réussie, retourne le token"),
        @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect"),
        @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        boolean success = loginService.login(request.getEmail(), request.getPassword());
        if (!success) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email ou mot de passe incorrect"));
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnexion de l'utilisateur", description = "Blackliste le JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Déconnexion réussie"),
        @ApiResponse(responseCode = "400", description = "Token manquant ou malformé")
    })
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Token manquant"));
        }
        String token = authHeader.substring(7);
        jwtService.blacklistToken(token);
        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));
    }
}
