package com.rubiojdev.todolist.auth.controllers;

import com.rubiojdev.todolist.auth.docs.AuthApiDocs;
import com.rubiojdev.todolist.auth.dtos.AuthResponse;
import com.rubiojdev.todolist.auth.dtos.LoginRequest;
import com.rubiojdev.todolist.auth.dtos.RefreshTokenRequest;
import com.rubiojdev.todolist.auth.dtos.RegisterRequest;
import com.rubiojdev.todolist.auth.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthApiDocs {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login (
            @RequestBody @Valid LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody @Valid RegisterRequest registerRequest) {

        AuthResponse response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody @Valid RefreshTokenRequest tokenRequest) {

        AuthResponse response = authService.refreshToken(tokenRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody @Valid RefreshTokenRequest tokenRequest) {

        authService.logout(tokenRequest);
        return ResponseEntity.noContent().build();
    }
}
