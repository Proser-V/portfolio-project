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

/**
 * Filtre de sécurité JWT personnalisé pour Spring Security.
 * 
 * Ce filtre intercepte chaque requête HTTP et :
 * - extrait le JWT depuis l'en-tête Authorization ou le cookie,
 * - vérifie sa validité et si le token est blacklisté,
 * - authentifie l'utilisateur dans le SecurityContext si le JWT est valide.
 * 
 * Hérite de {@link OncePerRequestFilter} pour s'assurer que le filtre
 * est exécuté une seule fois par requête.
 */
@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final UserRepo userRepository;

    /**
     * Constructeur injectant le service JWT et le repository utilisateur.
     * 
     * @param jwtService service pour la gestion et la validation des tokens JWT
     * @param userRepository repository pour accéder aux données des utilisateurs
     */
    public JwtAuthenticationFilter(JwtService jwtService, UserRepo userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    /**
     * Méthode principale exécutée pour chaque requête HTTP.
     * 
     * @param request requête HTTP entrante
     * @param response réponse HTTP sortante
     * @param filterChain chaîne de filtres Spring Security
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        logger.info("Filtre JWT: Traitement de la requête pour {}", request.getRequestURI());
        String jwt = null;

        // Extraction du JWT depuis l'en-tête Authorization si présent
        String authHeader = request.getHeader("Authorization");
        logger.info("En-tête Authorization reçu: {}", authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            logger.info("JWT extrait de l'en-tête Authorization: {}", jwt);
        }

        // Extraction du JWT depuis le cookie si aucun Authorization header
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

        // Ignorer le filtre pour la route logout
        if (request.getServletPath().equals("/api/users/logout")) {
            logger.info("Ignoring /api/users/logout");
            filterChain.doFilter(request, response);
            return;
        }

        // Si aucun JWT trouvé, continuer la chaîne de filtres
        if (jwt == null) {
            logger.warn("Aucun JWT trouvé pour {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // Vérifier si le JWT est blacklisté
        if (jwtService.isTokenBlacklisted(jwt)) {
            logger.warn("JWT blacklisté: {}", jwt);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token invalide ou expiré\"}");
            return;
        }

        // Extraction de l'utilisateur depuis le JWT
        final String username = jwtService.extractUsername(jwt);
        logger.info("Utilisateur extrait du JWT: {}", username);

        // Authentification dans le contexte de sécurité si nécessaire
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

        // Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }

    /**
     * Détermine les routes pour lesquelles ce filtre ne doit pas s'exécuter.
     * 
     * @param request requête HTTP
     * @return true si le filtre doit être ignoré pour cette requête
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/api/clients/register")
                || path.equals("/api/artisans/register")
                || path.equals("/api/users/login")
                || path.equals("/api/artisans/debug/categories")
                || path.startsWith("/api/geocode")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api/docs");
    }
}
