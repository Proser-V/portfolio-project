package com.atelierlocal.controller;

import com.atelierlocal.dto.LoginRequest;
import com.atelierlocal.security.CustomUserDetailsService;
import com.atelierlocal.security.JwtService;
import com.atelierlocal.service.LoginService;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@exemple.com");
        request.setPassword("password");
        String fakeToken = "fake-jwt-token";

        when(loginService.login(anyString(), anyString())).thenReturn(true);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mock(UserDetails.class));
        when(jwtService.generateToken(any())).thenReturn(fakeToken);

        ResponseEntity<?> response = loginController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("Set-Cookie"));

        String cookieHeader = response.getHeaders().getFirst("Set-Cookie");
        assertTrue(cookieHeader.contains(fakeToken));
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
        String token = "fake-token";

        doNothing().when(jwtService).blacklistToken(token);

        // Appel du logout
        ResponseEntity<Void> response = loginController.logout(token);

        // Vérifie que le token a été blacklister
        verify(jwtService).blacklistToken(token);

        // Vérifie le statut HTTP
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody()); // Pas de corps
    }

    @Test
    void testLogoutBadRequest() {
        String token = null;

        // Appel du logout
        ResponseEntity<Void> response = loginController.logout(token);

        // Aucun token à blacklister
        verifyNoInteractions(jwtService);

        // Vérifie le statut HTTP
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Toujours OK dans ce design minimal
        assertNull(response.getBody());
    }
}
