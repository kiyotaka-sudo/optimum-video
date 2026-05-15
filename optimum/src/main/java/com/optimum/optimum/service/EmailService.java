package com.optimum.optimum.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import com.optimum.optimum.model.SubscriptionPlan;
import com.optimum.optimum.model.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String FROM = "adminkeyce@gmail.com";
    private static final String APP_NAME = "Optimum Video";

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendWelcomeEmail(UserAccount user) {
        String subject = "Bienvenue sur " + APP_NAME + " !";
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;background:#141414;color:#fff;padding:40px;border-radius:8px;">
              <h1 style="color:#e50914;">Bienvenue sur <span style="color:#fff;">Optimum Video</span></h1>
              <p>Bonjour <strong>%s</strong>,</p>
              <p>Votre compte a été créé avec succès. Vous pouvez maintenant vous connecter et profiter de notre catalogue.</p>
              <p><strong>Email :</strong> %s</p>
              <a href="http://localhost:9092/login" style="display:inline-block;margin-top:20px;padding:12px 24px;background:#e50914;color:#fff;text-decoration:none;border-radius:4px;font-weight:bold;">
                Se connecter
              </a>
              <p style="margin-top:30px;color:#888;font-size:0.85rem;">Si vous n'êtes pas à l'origine de cette inscription, ignorez cet email.</p>
            </div>
            """.formatted(user.getFirstName() != null ? user.getFirstName() : user.getUsername(), user.getEmail());
        sendHtml(user.getEmail(), subject, html);
    }

    @Async
    public void sendPaymentConfirmation(UserAccount user, SubscriptionPlan plan, String billingCycle, String transactionId) {
        String subject = "Confirmation de paiement – " + plan.getName();
        double amount = "YEARLY".equals(billingCycle) ? plan.getPriceYearly() : plan.getPriceMonthly();
        String cycle = "YEARLY".equals(billingCycle) ? "Annuel" : "Mensuel";
        String expiry = user.getSubscriptionEnd() != null ? user.getSubscriptionEnd().toString() : "N/A";
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;background:#141414;color:#fff;padding:40px;border-radius:8px;">
              <h1 style="color:#e50914;">Confirmation de paiement</h1>
              <p>Bonjour <strong>%s</strong>,</p>
              <p>Votre abonnement <strong>%s</strong> (%s) a bien été activé.</p>
              <table style="width:100%%;border-collapse:collapse;margin:20px 0;">
                <tr style="border-bottom:1px solid #333;">
                  <td style="padding:10px;color:#aaa;">Montant</td>
                  <td style="padding:10px;text-align:right;"><strong>%.2f €</strong></td>
                </tr>
                <tr style="border-bottom:1px solid #333;">
                  <td style="padding:10px;color:#aaa;">Cycle</td>
                  <td style="padding:10px;text-align:right;">%s</td>
                </tr>
                <tr style="border-bottom:1px solid #333;">
                  <td style="padding:10px;color:#aaa;">Expiration</td>
                  <td style="padding:10px;text-align:right;">%s</td>
                </tr>
                <tr>
                  <td style="padding:10px;color:#aaa;">Transaction ID</td>
                  <td style="padding:10px;text-align:right;font-family:monospace;font-size:0.85rem;">%s</td>
                </tr>
              </table>
              <a href="http://localhost:9092/" style="display:inline-block;margin-top:20px;padding:12px 24px;background:#e50914;color:#fff;text-decoration:none;border-radius:4px;font-weight:bold;">
                Accéder à Optimum Video
              </a>
            </div>
            """.formatted(
                user.getFirstName() != null ? user.getFirstName() : user.getUsername(),
                plan.getName(), cycle, amount, cycle, expiry, transactionId);
        sendHtml(user.getEmail(), subject, html);
    }

    @Async
    public void sendSubscriptionExpiryReminder(UserAccount user, long daysLeft) {
        String subject = "⚠️ Votre abonnement expire dans " + daysLeft + " jour(s) – " + APP_NAME;
        String planName = user.getSubscriptionPlan() != null ? user.getSubscriptionPlan().getName() : "votre abonnement";
        String expiry = user.getSubscriptionEnd() != null ? user.getSubscriptionEnd().toString() : "bientôt";
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;background:#141414;color:#fff;padding:40px;border-radius:8px;">
              <h1 style="color:#e50914;">⚠️ Renouvellement d'abonnement</h1>
              <p>Bonjour <strong>%s</strong>,</p>
              <p>Votre abonnement <strong>%s</strong> expire le <strong>%s</strong> (dans <strong>%d jour(s)</strong>).</p>
              <p>Renouvelez maintenant pour continuer à profiter du contenu sans interruption.</p>
              <a href="http://localhost:9092/plans" style="display:inline-block;margin-top:20px;padding:12px 24px;background:#e50914;color:#fff;text-decoration:none;border-radius:4px;font-weight:bold;">
                Renouveler mon abonnement
              </a>
              <p style="margin-top:30px;color:#888;font-size:0.85rem;">
                Si vous avez déjà renouvelé, ignorez cet email.
              </p>
            </div>
            """.formatted(
                user.getFirstName() != null ? user.getFirstName() : user.getUsername(),
                planName, expiry, daysLeft);
        sendHtml(user.getEmail(), subject, html);
    }

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(FROM, APP_NAME);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(msg);
            log.info("Email envoyé à {} : {}", to, subject);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Échec envoi email à {} : {}", to, e.getMessage());
        }
    }
}
