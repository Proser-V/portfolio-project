package com.atelierlocal.security;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Intercepteur de handshake WebSocket pour l'authentification via JWT.
 * 
 * Ce composant :
 * - intercepte le handshake WebSocket,
 * - extrait le token JWT depuis l'en-tête Authorization ou le cookie,
 * - valide le token et récupère l'utilisateur associé,
 * - stocke les informations d'utilisateur dans les attributs de session WebSocket.
 */
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);
    
    private final JwtService jwtService;
    
    /**
     * Constructeur injectant le service JWT.
     * 
     * @param jwtService service pour la validation et extraction des JWT
     */
    public JwtHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    
    /**
     * Méthode exécutée avant le handshake WebSocket.
     * 
     * @param request requête HTTP entrante
     * @param response réponse HTTP sortante
     * @param wsHandler handler WebSocket
     * @param attributes attributs de session WebSocket
     * @return true si le handshake est autorisé, false sinon
     * @throws Exception en cas d'erreur lors de l'interception
     */
    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        
        logger.info("Handshake WebSocket - URI: {}", request.getURI());
        String token = null;
        
        // 1. Essayer d'extraire le JWT depuis le header Authorization (STOMP l'envoie ici)
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            logger.info("Token JWT trouvé dans le header Authorization");
        }
        
        // 2. Sinon, tenter d'extraire le JWT depuis les cookies (fallback)
        if (token == null && request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            Cookie[] cookies = servletRequest.getCookies();
            
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        token = cookie.getValue();
                        logger.info("Token JWT trouvé dans le cookie");
                        break;
                    }
                }
            }
        }
        
        // 3. Valider le token JWT
        if (token != null) {
            try {
                String username = jwtService.extractUsername(token);
                
                if (username != null && !jwtService.isTokenExpired(token)) {
                    logger.info("Token valide pour: {}", username);
                    
                    // Stocker les informations dans les attributs de session WebSocket
                    attributes.put("jwt", token);
                    attributes.put("username", username);
                    
                    return true;
                } else {
                    logger.warn("Token invalide ou expiré");
                }
            } catch (Exception e) {
                logger.error("Erreur lors de la validation du token: {}", e.getMessage());
            }
        } else {
            logger.warn("Aucun token JWT trouvé (ni header Authorization, ni cookie)");
        }
        
        // Refuser la connexion si pas de token valide
        logger.error("Handshake refusé - pas de token valide");
        return false;
    }
    
    /**
     * Méthode exécutée après le handshake WebSocket.
     * 
     * @param request requête HTTP entrante
     * @param response réponse HTTP sortante
     * @param wsHandler handler WebSocket
     * @param exception exception survenue lors du handshake (null si aucune)
     */
    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        
        if (exception != null) {
            logger.error("Erreur après handshake: {}", exception.getMessage());
        } else {
            logger.info("Handshake terminé avec succès");
        }
    }
}
