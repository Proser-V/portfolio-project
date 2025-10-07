package com.atelierlocal.controller;

import com.atelierlocal.dto.LoginRequest;
import com.atelierlocal.security.CustomUserDetailsService;
import com.atelierlocal.security.JwtService;
import com.atelierlocal.service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    private LoginController loginController;
    private LoginService loginService;
    private JwtService jwtService;
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        loginService = mock(LoginService.class);
        jwtService = mock(JwtService.class);
        userDetailsService = mock(CustomUserDetailsService.class);
        loginController = new LoginController(loginService, jwtService, userDetailsService);
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(loginService.login(request.getEmail(), request.getPassword())).thenReturn(true);

        UserDetails mockUser = User.withUsername(request.getEmail())
                .password("password").roles("CLIENT").build();

        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(mockUser);
        when(jwtService.generateToken(mockUser)).thenReturn("fake-jwt-token");

        ResponseEntity<?> response = loginController.login(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("fake-jwt-token", body.get("token"));

        verify(loginService).login(request.getEmail(), request.getPassword());
        verify(userDetailsService).loadUserByUsername(request.getEmail());
        verify(jwtService).generateToken(mockUser);
    }

    @Test
    void testLoginFailure() {
        LoginRequest request = new LoginRequest();
        request.setEmail("wrong@example.com");
        request.setPassword("badpass");

        when(loginService.login(request.getEmail(), request.getPassword())).thenReturn(false);

        ResponseEntity<?> response = loginController.login(request);

        assertNotNull(response);
        assertEquals(401, response.getStatusCode().value());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Email ou mot de passe incorrect", body.get("error"));

        verify(loginService).login(request.getEmail(), request.getPassword());
        verifyNoInteractions(userDetailsService, jwtService);
}


    @Test
    void testLogoutSuccess() {
        String token = "Bearer fake-token";

        doNothing().when(jwtService).blacklistToken("fake-token");

        ResponseEntity<?> response = loginController.logout(token);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Déconnexion réussie", body.get("message"));

        verify(jwtService).blacklistToken("fake-token");
    }

    @Test
    void testLogoutBadRequest() {
        String token = "invalid-header";

        ResponseEntity<?> response = loginController.logout(token);

        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Token manquant", body.get("error"));

        verifyNoInteractions(jwtService);
    }
}
