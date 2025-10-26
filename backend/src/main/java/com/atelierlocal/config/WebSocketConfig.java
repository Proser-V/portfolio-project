package com.atelierlocal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.atelierlocal.security.JwtHandshakeInterceptor;

/**
 * Classe de configuration pour WebSocket avec STOMP.
 * Configure les endpoints, le broker de messages et l'intercepteur JWT pour sécuriser les connexions.
 */
@Configuration
@EnableWebSocketMessageBroker // Active le support WebSocket avec message broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    /**
     * Constructeur pour injecter l'intercepteur JWT.
     * L'intercepteur permet de vérifier le token JWT lors de la phase de handshake WebSocket.
     * 
     * @param jwtHandshakeInterceptor l'intercepteur de handshake pour JWT
     */
    public WebSocketConfig(JwtHandshakeInterceptor jwtHandshakeInterceptor) {
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }

    /**
     * Configuration du broker de messages.
     * 
     * - enableSimpleBroker : active un broker simple en mémoire pour les destinations "/queue" et "/topic"
     * - setApplicationDestinationPrefixes : préfixe pour les messages envoyés par le client vers le serveur
     * - setUserDestinationPrefix : préfixe pour les messages destinés à un utilisateur spécifique
     * 
     * @param config objet MessageBrokerRegistry pour configurer le broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue", "/topic"); // Broker simple pour les topics et queues
        config.setApplicationDestinationPrefixes("/app"); // Messages envoyés au serveur
        config.setUserDestinationPrefix("/user"); // Messages ciblés à un utilisateur
    }

    /**
     * Enregistrement des endpoints STOMP.
     * 
     * - addEndpoint("/ws") : URL pour établir la connexion WebSocket
     * - setAllowedOriginPatterns("*") : autorise toutes les origines (CORS)
     * - addInterceptors(jwtHandshakeInterceptor) : ajoute l'intercepteur JWT pour sécuriser la handshake
     * - withSockJS() : fallback SockJS si WebSocket natif indisponible
     * 
     * @param registry objet StompEndpointRegistry pour enregistrer les endpoints
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(jwtHandshakeInterceptor)
                .withSockJS();
    }
}
