package com.atelierlocal.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.atelierlocal.security.CustomUserDetailsService;
import com.atelierlocal.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Classe de configuration de la sécurité de l'application.
 * Configure Spring Security avec :
 * - Gestion des mots de passe via Argon2
 * - Gestion de l'authentification JWT
 * - CORS pour autoriser les requêtes depuis le frontend
 * - Autorisation des endpoints publics et protection des endpoints privés
 * - Gestion du logout
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true) // Active les annotations @PreAuthorize/@PostAuthorize
public class SecurityConfig {

    /**
     * Bean pour le chiffrement des mots de passe avec Argon2.
     * Paramètres :
     *  - saltLength: 16
     *  - hashLength: 32
     *  - parallelism: 1
     *  - memory: 65536 KB
     *  - iterations: 3
     * 
     * @return un encodeur Argon2PasswordEncoder
     */
    @Bean
    public Argon2PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 65536, 3);
    }

    /**
     * Bean pour l'AuthenticationManager, nécessaire pour l'authentification.
     * 
     * @param authConfig configuration de l'authentification Spring
     * @return l'AuthenticationManager configuré
     * @throws Exception en cas d'erreur lors de la récupération du manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Bean principal de configuration du SecurityFilterChain.
     * Configure :
     * - CSRF désactivé (car API stateless)
     * - CORS avec la configuration définie dans corsConfigurationSource()
     * - Endpoints publics et sécurisés
     * - JWT filter avant UsernamePasswordAuthenticationFilter
     * - Gestion du logout et suppression du cookie JWT
     * 
     * @param http objet HttpSecurity
     * @param jwtFilter filtre JWT pour authentification des requêtes
     * @param userDetailsService service pour charger les utilisateurs
     * @param passwordEncoder encodeur de mot de passe Argon2
     * @return SecurityFilterChain configuré
     * @throws Exception en cas d'erreur lors de la configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, 
                                           JwtAuthenticationFilter jwtFilter,
                                           CustomUserDetailsService userDetailsService,
                                           Argon2PasswordEncoder passwordEncoder) throws Exception {
        return http
            // Désactivation CSRF car API stateless
            .csrf(csrf -> csrf.disable())
            // Configuration CORS
            .cors(c -> c.configurationSource(corsConfigurationSource()))
            // Configuration des autorisations
            .authorizeHttpRequests(auth -> auth
                // Endpoints publics (accessible sans authentification)
                .requestMatchers(
                    "/home", "/", "/api/users/logout", "/api/users/login",
                    "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**",
                    "/api/artisans/**",
                    "/api/clients/**", 
                    "/api/artisan-category/**",
                    "/api/geocode/**",
                    "/api/avatar/**",
                    "/api/event-categories/**",
                    "/api/askings/**"
                ).permitAll()
                // Tout le reste nécessite une authentification
                .anyRequest().authenticated()
            )
            // Service pour charger les utilisateurs
            .userDetailsService(userDetailsService)
            // Ajout du filtre JWT avant le filtre standard UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            // Gestion du logout
            .logout(logout -> logout
                .logoutUrl("/api/users/logout")
                .clearAuthentication(true)
                .deleteCookies("jwt")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK); // Retourne 200 OK
                })
            )
            .build();
    }

    /**
     * Bean pour la configuration CORS.
     * Permet d'autoriser le frontend (localhost:3000) à communiquer avec l'API.
     * - Autorise tous les headers
     * - Expose les headers Authorization et Set-Cookie
     * - Autorise les méthodes GET, POST, PUT, DELETE, PATCH
     * 
     * @return CorsConfigurationSource configuré
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowCredentials(true); // Autorise l'envoi des cookies
        config.setAllowedOriginPatterns(List.of("http://localhost:3000")); // Origine autorisée
        config.setExposedHeaders(List.of("Authorization", "Set-Cookie")); // Headers exposés au frontend
        config.setAllowedHeaders(List.of("*")); // Tous les headers autorisés
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH")); // Méthodes autorisées

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Appliquer à toutes les routes
        return source;
    }
}
