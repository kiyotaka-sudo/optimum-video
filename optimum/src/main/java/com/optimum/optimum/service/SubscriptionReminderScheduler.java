package com.optimum.optimum.service;

import com.optimum.optimum.model.UserAccount;
import com.optimum.optimum.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class SubscriptionReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionReminderScheduler.class);

    private final UserAccountRepository accountRepository;
    private final EmailService emailService;

    public SubscriptionReminderScheduler(UserAccountRepository accountRepository, EmailService emailService) {
        this.accountRepository = accountRepository;
        this.emailService = emailService;
    }

    // Tous les jours à 9h00
    @Scheduled(cron = "0 0 9 * * *")
    public void sendExpiryReminders() {
        log.info("Vérification des abonnements expirant bientôt...");

        LocalDate today = LocalDate.now();

        // Rappel 7 jours avant
        List<UserAccount> expiring7 = accountRepository.findBySubscriptionEndBetween(
                today.plusDays(6), today.plusDays(7));
        for (UserAccount user : expiring7) {
            log.info("Rappel 7j envoyé à {}", user.getEmail());
            emailService.sendSubscriptionExpiryReminder(user, 7);
        }

        // Rappel 3 jours avant
        List<UserAccount> expiring3 = accountRepository.findBySubscriptionEndBetween(
                today.plusDays(2), today.plusDays(3));
        for (UserAccount user : expiring3) {
            log.info("Rappel 3j envoyé à {}", user.getEmail());
            emailService.sendSubscriptionExpiryReminder(user, 3);
        }

        // Rappel 1 jour avant
        List<UserAccount> expiring1 = accountRepository.findBySubscriptionEndBetween(
                today.plusDays(0), today.plusDays(1));
        for (UserAccount user : expiring1) {
            log.info("Rappel 1j envoyé à {}", user.getEmail());
            emailService.sendSubscriptionExpiryReminder(user, 1);
        }

        log.info("Rappels envoyés : 7j={}, 3j={}, 1j={}",
                expiring7.size(), expiring3.size(), expiring1.size());
    }
}
