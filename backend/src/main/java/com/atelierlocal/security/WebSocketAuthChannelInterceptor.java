package com.atelierlocal.security;

import java.security.Principal;
import java.util.Optional;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import com.atelierlocal.model.User;
import com.atelierlocal.repository.UserRepo;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthChannelInterceptor.class);
    
    private final JwtService jwtService;
    private final UserRepo userRepo;

    public WebSocketAuthChannelInterceptor(JwtService jwtService, UserRepo userRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            logger.info("🔌 Tentative de connexion WebSocket");
            
            // Récupérer le token depuis le header Authorization
            String token = accessor.getFirstNativeHeader("Authorization");
            
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                logger.info("🔑 Token trouvé dans Authorization header");
            } else {
                logger.warn("⚠️ Pas de token dans Authorization header");
            }

            if (token != null) {
                try {
                    // Vérifier la validité du token
                    String username = jwtService.extractUsername(token);
                    
                    if (username != null && jwtService.isTokenValid(token, null)) {
                        logger.info("Token valide pour: {}", username);
                        
                        Optional<User> optUser = userRepo.findByEmail(username);
                        
                        if (optUser.isPresent()) {
                            User user = optUser.get();
                            
                            // Créer le Principal avec l'email (pas l'UUID)
                            // Spring utilise le nom du Principal pour router les messages
                            Principal principal = new UsernamePasswordAuthenticationToken(
                                username, // Utiliser l'email comme nom
                                null, 
                                user.getAuthorities()
                            );
                            
                            accessor.setUser(principal);
                            logger.info("Utilisateur authentifié: {} (ID: {})", username, user.getId());
                        } else {
                            logger.error("Utilisateur non trouvé: {}", username);
                        }
                    } else {
                        logger.error("Token invalide ou expiré");
                    }
                } catch (Exception e) {
                    logger.error("Erreur lors de l'authentification WebSocket: {}", e.getMessage(), e);
                }
            } else {
                logger.error("Aucun token fourni");
            }
        }

        return message;
    }
}