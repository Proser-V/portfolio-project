package com.atelierlocal.security;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);
    
    private final JwtService jwtService;

    public JwtHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request, 
            ServerHttpResponse response,
            WebSocketHandler wsHandler, 
            Map<String, Object> attributes) throws Exception {
        
        logger.info("🤝 Handshake WebSocket - URI: {}", request.getURI());
        
        // Récupérer le token depuis les headers
        String authHeader = request.getHeaders().getFirst("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logger.info("🔑 Token trouvé dans handshake");
            
            try {
                // Vérifier la validité du token (sans UserDetails)
                String username = jwtService.extractUsername(token);
                
                if (username != null && !jwtService.isTokenExpired(token)) {
                    logger.info("✅ Token valide pour: {}", username);
                    // Stocker le token dans les attributs pour l'utiliser plus tard
                    attributes.put("jwt", token);
                    attributes.put("username", username);
                    return true;
                } else {
                    logger.warn("⚠️ Token invalide ou expiré");
                }
            } catch (Exception e) {
                logger.error("❌ Erreur lors de la validation du token: {}", e.getMessage());
            }
        } else {
            logger.warn("⚠️ Pas de token Authorization dans le handshake");
        }
        
        // Permettre la connexion même sans token valide pour le moment
        // Le ChannelInterceptor s'occupera de l'authentification
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request, 
            ServerHttpResponse response,
            WebSocketHandler wsHandler, 
            Exception exception) {
        
        if (exception != null) {
            logger.error("❌ Erreur après handshake: {}", exception.getMessage());
        } else {
            logger.info("✅ Handshake terminé avec succès");
        }
    }
}