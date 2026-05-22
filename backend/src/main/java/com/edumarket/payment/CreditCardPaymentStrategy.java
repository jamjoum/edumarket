package com.edumarket.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Stratégie de paiement par carte bancaire (Visa / Mastercard).
 * Simule une intégration avec un PSP (ex: Stripe).
 */
@Slf4j
@Component("creditCardPayment")
public class CreditCardPaymentStrategy implements PaymentStrategy {

    @Override
    public String getMethodName() {
        return "CREDIT_CARD";
    }

    @Override
    public PaymentResult process(BigDecimal amount, String userId, String details) {
        log.info("[CreditCard] Traitement paiement {} EUR pour user {}", amount, userId);

        // Simulation appel PSP (Stripe, etc.)
        // En prod : appel REST vers l'API Stripe
        var transactionId = "ch_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);

        log.info("[CreditCard] Transaction approuvée – ref: {}", transactionId);
        return PaymentResult.ok(transactionId);
    }

    @Override
    public boolean refund(String providerRef, BigDecimal amount) {
        log.info("[CreditCard] Remboursement {} EUR – ref: {}", amount, providerRef);
        // Simulation appel refund API
        return true;
    }
}
