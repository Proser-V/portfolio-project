package com.atelierlocal.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.atelierlocal.model.User;
import com.atelierlocal.repository.UserRepo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final UserRepo userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepo userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        logger.info("Filtre JWT: Traitement de la requête pour {}", request.getRequestURI());
        String jwt = null;

        // Vérifier l'en-tête Authorization
        String authHeader = request.getHeader("Authorization");
        logger.info("En-tête Authorization reçu: {}", authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            logger.info("JWT extrait de l'en-tête Authorization: {}", jwt);
        }

        // Vérifier le cookie jwt si Authorization n'est pas trouvé
        if (jwt == null) {
            final String cookieHeader = request.getHeader("Cookie");
            logger.info("En-tête Cookie reçu: {}", cookieHeader);
            if (cookieHeader != null && cookieHeader.contains("jwt=")) {
                String[] cookies = cookieHeader.split("; ");
                for (String cookie : cookies) {
                    if (cookie.startsWith("jwt=")) {
                        jwt = cookie.substring("jwt=".length());
                        logger.info("JWT extrait du cookie: {}", jwt);
                        break;
                    }
                }
            }
        }

        if (request.getServletPath().equals("/api/users/logout")) {
            logger.info("Ignoring /api/users/logout");
            filterChain.doFilter(request, response);
            return;
        }

        if (jwt == null) {
            logger.warn("Aucun JWT trouvé pour {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtService.isTokenBlacklisted(jwt)) {
            logger.warn("JWT blacklisté: {}", jwt);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token invalide ou expiré\"}");
            return;
        }

        final String username = jwtService.extractUsername(jwt);
        logger.info("Utilisateur extrait du JWT: {}", username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByEmail(username).orElse(null);
            logger.info("Utilisateur trouvé dans la base: {}", user != null ? user.getEmail() : "null");

            if (user != null && jwtService.isTokenValid(jwt, user)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("Authentification définie pour: {}", username);
            } else {
                logger.warn("JWT invalide ou utilisateur non trouvé pour: {}", username);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/api/clients/register")
                || path.equals("/api/artisans/register")
                || path.equals("/api/users/login")
                || path.equals("/api/artisans/debug/categories")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api/docs");
    }
}