package com.atelierlocal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.LoginRequest;
import com.atelierlocal.service.LoginService;

@RestController
@RequestMapping("/api/users")
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        boolean success = loginService.login(request.getEmail(), request.getPassword());
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

    @GetMapping("/login")
    public String loginTest() {
        return "Tarte atteint";
    }
}
