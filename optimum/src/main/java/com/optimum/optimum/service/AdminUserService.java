package com.optimum.optimum.service;

import com.optimum.optimum.model.*;
import com.optimum.optimum.repository.SubscriptionPlanRepository;
import com.optimum.optimum.repository.UserAccountRepository;
import com.optimum.optimum.repository.UserProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AdminUserService {

    private final UserAccountRepository accounts;
    private final UserProfileRepository profiles;
    private final SubscriptionPlanRepository plans;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PaymentSimulationService paymentService;

    public AdminUserService(UserAccountRepository accounts,
                            UserProfileRepository profiles,
                            SubscriptionPlanRepository plans,
                            PasswordEncoder passwordEncoder,
                            EmailService emailService,
                            PaymentSimulationService paymentService) {
        this.accounts = accounts;
        this.profiles = profiles;
        this.plans = plans;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.paymentService = paymentService;
    }

    public List<UserAccount> listAll() {
        return accounts.findAll();
    }

    /** Crée un nouveau compte utilisateur ou admin depuis l'espace admin */
    public UserAccount createUser(String email, String rawPassword,
                                  String firstName, String lastName, UserRole role) {
        if (accounts.findByEmail(email.trim().toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("Un compte existe déjà avec cet email : " + email);
        }
        String username = firstName + " " + lastName;
        UserAccount account = accounts.save(new UserAccount(
                email, username,
                passwordEncoder.encode(rawPassword),
                firstName, lastName, role));
        profiles.save(new UserProfile(account, firstName, false, MaturityRating.R, true));
        emailService.sendWelcomeEmail(account);
        return account;
    }

    /** Modifie prénom, nom et rôle d'un utilisateur existant */
    public UserAccount updateUser(UUID id, String firstName, String lastName, UserRole role) {
        UserAccount user = accounts.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + id));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(firstName + " " + lastName);
        user.setRole(role);
        return accounts.save(user);
    }

    /** Active ou suspend un compte */
    public boolean toggleActive(UUID id) {
        UserAccount user = accounts.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + id));
        user.setActive(!user.isActive());
        accounts.save(user);
        return user.isActive();
    }

    /** Supprime un compte et toutes ses données associées */
    public void deleteUser(UUID id) {
        UserAccount user = accounts.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + id));
        accounts.delete(user);
    }

    /**
     * Assigne un abonnement à un utilisateur.
     * Simule le paiement et active l'abonnement uniquement en cas de succès.
     * @return le PaymentResult pour afficher le statut dans l'interface
     */
    public PaymentSimulationService.PaymentResult assignSubscription(UUID userId,
                                                                     UUID planId,
                                                                     BillingCycle billingCycle,
                                                                     String cardLastFour,
                                                                     String paymentMethod) {
        UserAccount user = accounts.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + userId));
        SubscriptionPlan plan = plans.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan introuvable : " + planId));

        // Simulation du paiement
        PaymentSimulationService.PaymentResult result =
                paymentService.processPayment(user, plan, billingCycle, cardLastFour, paymentMethod);

        // Activation uniquement si paiement réussi
        if (result.status() == PaymentSimulationService.PaymentStatus.SUCCESS) {
            user.setSubscriptionPlan(plan);
            user.setBillingCycle(billingCycle);
            LocalDate expiry = (billingCycle == BillingCycle.YEARLY)
                    ? LocalDate.now().plusDays(365)
                    : LocalDate.now().plusDays(30);
            user.setSubscriptionEnd(expiry);
            accounts.save(user);
            emailService.sendPaymentConfirmation(user, plan, billingCycle.name(), result.transactionId());
        }

        return result;
    }

    /** Réinitialise le mot de passe d'un utilisateur */
    public void resetPassword(UUID id, String newPassword) {
        UserAccount user = accounts.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + id));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        accounts.save(user);
    }
}
