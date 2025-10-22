package com.atelierlocal.config;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.atelierlocal.model.User;
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.ClientRepo;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    
    private static final Logger log = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);
    
    @Autowired
    private ArtisanRepo artisanRepo;
    
    @Autowired
    private ClientRepo clientRepo;
    
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String email = (String) accessor.getSessionAttributes().get("username");
            log.info("Processing CONNECT: Session attributes = {}", accessor.getSessionAttributes());
        
            if (email != null) {
                // Vérifier d'abord dans ArtisanRepo
                User user = artisanRepo.findByEmail(email)
                    .map(User.class::cast)
                    .orElseGet(() -> clientRepo.findByEmail(email)
                        .map(User.class::cast)
                        .orElse(null));
            
                if (user != null) {
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            user.getAuthorities()
                        );
                
                    accessor.setUser(authentication);
                    log.info("✅ Authentification injectée pour: {} (Type: {})", email, user.getClass().getSimpleName());
                } else {
                    log.error("❌ Utilisateur non trouvé pour email: {}", email);
                    throw new SecurityException("User not found");
                }
            } else {
                log.error("❌ Pas d'email dans les attributs de session");
                throw new SecurityException("No email in session attributes");
            }
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            Principal principal = accessor.getUser();

            if (!(principal instanceof Authentication auth) || !auth.isAuthenticated()) {
                log.error("Tentative de SUBSCRIBE sans authentification valide");
                throw new SecurityException("Utilisateur non authentifié");
            }

            log.info("✅ SUBSCRIBE autorisé pour: {}", auth.getName());
        }

        return message;
    }
}