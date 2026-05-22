package com.edumarket.payment;

import java.math.BigDecimal;

/**
 * ┌─────────────────────────────────────────────────────────────┐
 * │              DESIGN PATTERN : STRATEGY                      │
 * ├─────────────────────────────────────────────────────────────┤
 * │  Problème : plusieurs méthodes de paiement (CB, PayPal,     │
 * │  Crypto) avec des comportements différents.                 │
 * │                                                             │
 * │  Solution : encapsuler chaque algorithme de paiement dans   │
 * │  une classe séparée implémentant une interface commune.     │
 * │  Le contexte (PaymentContext) délègue à la bonne stratégie  │
 * │  sans connaître son implémentation.                         │
 * └─────────────────────────────────────────────────────────────┘
 *
 * Interface commune à toutes les stratégies de paiement.
 */
public interface PaymentStrategy {

    /**
     * Identifiant unique de la stratégie (ex: "CREDIT_CARD").
     */
    String getMethodName();

    /**
     * Initie un paiement et retourne une référence provider.
     *
     * @param amount  montant à débiter
     * @param userId  identifiant de l'utilisateur
     * @param details données spécifiques au provider (token, email, wallet...)
     * @return référence de transaction chez le provider
     */
    PaymentResult process(BigDecimal amount, String userId, String details);

    /**
     * Effectue un remboursement partiel ou total.
     *
     * @param providerRef référence de la transaction originale
     * @param amount      montant à rembourser
     * @return true si le remboursement est initié avec succès
     */
    boolean refund(String providerRef, BigDecimal amount);

    /**
     * Résultat encapsulé d'un traitement de paiement.
     * Record Java 21.
     */
    record PaymentResult(
        boolean success,
        String  providerRef,
        String  message
    ) {
        public static PaymentResult ok(String ref) {
            return new PaymentResult(true, ref, "Paiement accepté");
        }
        public static PaymentResult failure(String reason) {
            return new PaymentResult(false, null, reason);
        }
    }
}
