package com.optimum.optimum.dto;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record RegisterRequest(String email, String password, String firstName, String lastName) {
    }

    public record LoginRequest(String email, String password) {
    }

    public record RefreshRequest(String refresh_token) {
    }

    public record TokenResponse(String access_token, String refresh_token, String token_type, int expires_in, String scope) {
    }
}
