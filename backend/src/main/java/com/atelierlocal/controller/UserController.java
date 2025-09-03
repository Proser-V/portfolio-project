package com.atelierlocal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import com.atelierlocal.dto.LoginRequest;

import com.atelierlocal.service.ClientService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final ClientService userService;

    public UserController(ClientService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        boolean success = userService.login(request.getEmail(), request.getPassword());
        if (success) {
            return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("Login successful");
        } else {
            return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid credentials");
        }
    }
}
