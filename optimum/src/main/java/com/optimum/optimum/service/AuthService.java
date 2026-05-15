package com.optimum.optimum.service;

import com.optimum.optimum.dto.AuthDtos.LoginRequest;
import com.optimum.optimum.dto.AuthDtos.RegisterRequest;
import com.optimum.optimum.dto.AuthDtos.TokenResponse;
import com.optimum.optimum.model.MaturityRating;
import com.optimum.optimum.model.UserAccount;
import com.optimum.optimum.model.UserProfile;
import com.optimum.optimum.model.UserRole;
import com.optimum.optimum.repository.UserAccountRepository;
import com.optimum.optimum.repository.UserProfileRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserAccountRepository accounts;
    private final UserProfileRepository profiles;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicy passwordPolicy;

    public AuthService(UserAccountRepository accounts, UserProfileRepository profiles,
                       PasswordEncoder passwordEncoder, PasswordPolicy passwordPolicy) {
        this.accounts = accounts;
        this.profiles = profiles;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicy = passwordPolicy;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        accounts.findByEmail(request.email()).ifPresent(existing -> {
            throw new IllegalArgumentException("Un compte existe deja avec cet email");
        });
        passwordPolicy.validate(request.password());
        String username = request.firstName() + " " + request.lastName();
        UserAccount account = accounts.save(new UserAccount(request.email(), username, passwordEncoder.encode(request.password()), request.firstName(), request.lastName(), UserRole.USER));
        profiles.save(new UserProfile(account, request.firstName(), false, MaturityRating.R, true));
        return tokenFor(account, "streaming:read streaming:write profile:read profile:write downloads:write");
    }

    public TokenResponse login(LoginRequest request) {
        UserAccount account = accounts.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Identifiants invalides"));
        if (!passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            throw new IllegalArgumentException("Identifiants invalides");
        }
        return tokenFor(account, "streaming:read streaming:write profile:read profile:write downloads:write");
    }

    public TokenResponse refresh(String refreshToken) {
        return new TokenResponse(fakeJwt("refresh"), UUID.randomUUID().toString(), "Bearer", 3600, "streaming:read streaming:write");
    }

    private TokenResponse tokenFor(UserAccount account, String scope) {
        String payload = account.getEmail() + ":" + Instant.now();
        String accessToken = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return new TokenResponse(accessToken, UUID.randomUUID().toString(), "Bearer", 3600, scope);
    }

    private String fakeJwt(String subject) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString((subject + ":" + Instant.now()).getBytes(StandardCharsets.UTF_8));
    }
}
