package com.edumarket.payment;

import com.edumarket.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * ┌─────────────────────────────────────────────────────────────┐
 * │              DESIGN PATTERN : STRATEGY – Contexte           │
 * ├─────────────────────────────────────────────────────────────┤
 * │  PaymentContext est le "context" du pattern Strategy.       │
 * │  Il sélectionne la bonne stratégie selon la méthode         │
 * │  de paiement et délègue le traitement.                      │
 * │                                                             │
 * │  Java 21 : Switch Expression exhaustif sur l'enum           │
 * │  PaymentMethod pour résoudre la stratégie.                  │
 * └─────────────────────────────────────────────────────────────┘
 */
@Slf4j
@Component
public class PaymentContext {

    private final Map<String, PaymentStrategy> strategies;

    /**
     * Spring injecte automatiquement toutes les implémentations de PaymentStrategy.
     * La Map est construite avec le nom du bean comme clé.
     */
    public PaymentContext(
        CreditCardPaymentStrategy creditCardPaymentStrategy,
        PayPalPaymentStrategy     paypalPaymentStrategy,
        CryptoPaymentStrategy     cryptoPaymentStrategy
    ) {
        this.strategies = Map.of(
            "CREDIT_CARD", creditCardPaymentStrategy,
            "PAYPAL",      paypalPaymentStrategy,
            "CRYPTO",      cryptoPaymentStrategy
        );
    }

    /**
     * Sélectionne la stratégie via un Switch Expression Java 21.
     *
     * Switch Expression (Java 14+, standard Java 21) :
     *   – Forme expression (retourne une valeur)
     *   – Exhaustivité vérifiée à la compilation
     *   – Pas de fall-through accidentel
     */
    public PaymentStrategy resolve(Payment.PaymentMethod method) {
        // ▼ Switch Expression Java 21 ▼
        String key = switch (method) {
            case CREDIT_CARD -> "CREDIT_CARD";
            case PAYPAL      -> "PAYPAL";
            case CRYPTO      -> "CRYPTO";
        };

        var strategy = strategies.get(key);
        if (strategy == null) {
            throw new IllegalArgumentException("Stratégie de paiement inconnue : " + method);
        }

        log.debug("Stratégie sélectionnée : {}", strategy.getMethodName());
        return strategy;
    }

    /**
     * Raccourci : traitement direct.
     */
    public PaymentStrategy.PaymentResult execute(
        Payment.PaymentMethod method,
        BigDecimal amount,
        String userId,
        String details
    ) {
        return resolve(method).process(amount, userId, details);
    }
}
