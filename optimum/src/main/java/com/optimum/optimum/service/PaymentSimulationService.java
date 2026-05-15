package com.optimum.optimum.service;

import com.optimum.optimum.model.BillingCycle;
import com.optimum.optimum.model.SubscriptionPlan;
import com.optimum.optimum.model.UserAccount;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentSimulationService {

    public enum PaymentStatus { SUCCESS, DECLINED, INSUFFICIENT_FUNDS }

    public record PaymentResult(
            PaymentStatus status,
            String transactionId,
            String message,
            double amount,
            String currency
    ) {}

    /**
     * Simule un paiement. Les 4 derniers chiffres "0000" simulent un refus,
     * "1111" simule des fonds insuffisants, tout autre valeur = succès.
     */
    public PaymentResult processPayment(UserAccount user,
                                        SubscriptionPlan plan,
                                        BillingCycle billingCycle,
                                        String cardLastFour,
                                        String paymentMethod) {

        double amount = (billingCycle == BillingCycle.YEARLY)
                ? plan.getPriceYearly()
                : plan.getPriceMonthly();

        // Simulation des cas d'erreur
        if ("0000".equals(cardLastFour)) {
            return new PaymentResult(
                    PaymentStatus.DECLINED,
                    null,
                    "Carte refusée par l'émetteur. Veuillez utiliser un autre moyen de paiement.",
                    amount,
                    plan.getCurrency()
            );
        }

        if ("1111".equals(cardLastFour)) {
            return new PaymentResult(
                    PaymentStatus.INSUFFICIENT_FUNDS,
                    null,
                    "Fonds insuffisants. Veuillez recharger votre compte ou utiliser une autre carte.",
                    amount,
                    plan.getCurrency()
            );
        }

        // Succès
        String transactionId = "OPT-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        String methodLabel = switch (paymentMethod) {
            case "ORANGE_MONEY" -> "Orange Money";
            case "MTN_MOMO"     -> "MTN Mobile Money";
            default             -> "Carte Visa (**** " + cardLastFour + ")";
        };

        return new PaymentResult(
                PaymentStatus.SUCCESS,
                transactionId,
                "Paiement de %.2f %s effectué via %s".formatted(amount, plan.getCurrency(), methodLabel),
                amount,
                plan.getCurrency()
        );
    }
}
