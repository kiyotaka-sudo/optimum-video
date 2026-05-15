package com.optimum.optimum.model;

import jakarta.persistence.*;
import java.time.LocalDate;
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

    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id")
    private SubscriptionPlan subscriptionPlan;

    private LocalDate subscriptionEnd;

    @Enumerated(EnumType.STRING)
    private BillingCycle billingCycle;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
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
        this.active = true;
    }

    public String getEmail() { return email; }

    public String getUsername() { return username; }

    public void setUsername(String username) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Nom utilisateur obligatoire");
        this.username = username.trim();
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Email invalide");
        this.email = email.trim().toLowerCase();
    }

    public String getPasswordHash() { return passwordHash; }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.length() < 8) throw new IllegalArgumentException("Mot de passe trop court");
        this.passwordHash = passwordHash;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDisplayName() { return username; }

    public List<UserProfile> getProfiles() { return profiles; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public SubscriptionPlan getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }

    public LocalDate getSubscriptionEnd() { return subscriptionEnd; }
    public void setSubscriptionEnd(LocalDate subscriptionEnd) { this.subscriptionEnd = subscriptionEnd; }

    public BillingCycle getBillingCycle() { return billingCycle; }
    public void setBillingCycle(BillingCycle billingCycle) { this.billingCycle = billingCycle; }

    public boolean hasActiveSubscription() {
        return subscriptionPlan != null && subscriptionEnd != null && !subscriptionEnd.isBefore(LocalDate.now());
    }

    public long daysUntilExpiry() {
        if (subscriptionEnd == null) return -1;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), subscriptionEnd);
    }
}
