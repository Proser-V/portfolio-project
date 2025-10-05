package com.atelierlocal.security;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

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

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserRepo userRepo;

    public WebSocketAuthChannelInterceptor(JwtService jwtService, UserRepo userRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) token = token.substring(7);

            if (token == null && accessor.getSessionAttributes() != null) {
                Object jwt = accessor.getSessionAttributes().get("jwt");
                if (jwt instanceof String) token = (String) jwt;
            }

            if (token != null && jwtService.isTokenValid(token, null)) {
                String username = jwtService.extractUsername(token);

                Optional<User> optUser = userRepo.findByEmail(username);
                if (optUser.isPresent()) {
                    User user = optUser.get();
                    UUID userId = user.getId();

                    Principal principal = new UsernamePasswordAuthenticationToken(userId.toString(), null, null);
                    accessor.setUser(principal);
                } else {
                }
            }
        }
        return message;
    }
}
