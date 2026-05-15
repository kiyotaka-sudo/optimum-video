package com.optimum.optimum.controller;

import com.optimum.optimum.dto.AuthDtos.LoginRequest;
import com.optimum.optimum.dto.AuthDtos.RefreshRequest;
import com.optimum.optimum.dto.AuthDtos.RegisterRequest;
import com.optimum.optimum.dto.AuthDtos.TokenResponse;
import com.optimum.optimum.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private final AuthService authService;

    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    TokenResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    TokenResponse refresh(@RequestBody RefreshRequest request) {
        return authService.refresh(request.refresh_token());
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}
