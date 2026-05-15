package com.optimum.optimum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class UserAccount extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String username;
    private String passwordHash;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "account")
    private List<UserProfile> profiles = new ArrayList<>();

    protected UserAccount() {
    }

    public UserAccount(String email, String username, String passwordHash, String firstName, String lastName, UserRole role) {
        setEmail(email);
        setUsername(username);
        setPasswordHash(passwordHash);
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role == null ? UserRole.USER : role;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Nom utilisateur obligatoire");
        }
        this.username = username.trim();
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email invalide");
        }
        this.email = email.trim().toLowerCase();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.length() < 8) {
            throw new IllegalArgumentException("Mot de passe trop court");
        }
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDisplayName() {
        return username;
    }

    public List<UserProfile> getProfiles() {
        return profiles;
    }

    public UserRole getRole() {
        return role;
    }
}
