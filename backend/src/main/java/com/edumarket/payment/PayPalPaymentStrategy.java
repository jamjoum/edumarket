package com.edumarket.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Stratégie de paiement PayPal.
 * Simule une intégration avec l'API PayPal Orders v2.
 */
@Slf4j
@Component("paypalPayment")
public class PayPalPaymentStrategy implements PaymentStrategy {

    @Override
    public String getMethodName() {
        return "PAYPAL";
    }

    @Override
    public PaymentResult process(BigDecimal amount, String userId, String details) {
        log.info("[PayPal] Initiation paiement {} EUR pour user {}", amount, userId);

        // Simulation : en prod, créer un order PayPal, rediriger l'utilisateur
        // puis capturer le paiement après retour
        if (details == null || details.isBlank()) {
            return PaymentResult.failure("Email PayPal manquant");
        }

        var orderId = "PAYPAL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[PayPal] Order créé – orderId: {}", orderId);
        return PaymentResult.ok(orderId);
    }

    @Override
    public boolean refund(String providerRef, BigDecimal amount) {
        log.info("[PayPal] Remboursement {} EUR – orderId: {}", amount, providerRef);
        return true;
    }
}
