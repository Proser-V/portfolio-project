package com.atelierlocal.security;

import java.io.IOException;
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

        // Récupération du header Cookie
        final String cookieHeader = request.getHeader("Cookie");
        String jwt = null;

        // Extraction du JWT depuis le cookie nommé "jwt"
        if (cookieHeader != null && cookieHeader.contains("jwt=")) {
            String[] cookies = cookieHeader.split("; ");
            for (String cookie : cookies) {
                if (cookie.startsWith("jwt=")) {
                    jwt = cookie.substring("jwt=".length());
                    break;
                }
            }
        }

        // Si aucun JWT n'est trouvé, on passe au filtre suivant
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Vérification si le token est blacklisté
        if (jwtService.isTokenBlacklisted(jwt)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token invalide ou expiré\"}");
            return;
        }

        // Extraction du username à partir du token
        final String username = jwtService.extractUsername(jwt);

        // Vérifier si l'utilisateur n'est pas déjà authentifié
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Récupération de l'entité User depuis la base
            User user = userRepository.findByEmail(username).orElse(null);

            if (user != null && jwtService.isTokenValid(jwt, user)) {
                // Création d'un objet Authentication avec l'entité User
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                // Ajout des détails de la requête
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // On place l'objet Authentication dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // On continue la chaîne de filtres
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // Exclure certaines routes publiques
        String path = request.getServletPath();
        return path.equals("/api/clients/register")
                || path.equals("/api/artisans/register")
                || path.equals("/api/users/login")
                || path.equals("/api/artisans/debug/categories")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api/docs");
    }
}
