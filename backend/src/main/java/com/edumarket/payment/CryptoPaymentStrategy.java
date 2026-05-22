package com.edumarket.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Stratégie de paiement par cryptomonnaie (USDC / ETH).
 * Simule une intégration avec Coinbase Commerce.
 */
@Slf4j
@Component("cryptoPayment")
public class CryptoPaymentStrategy implements PaymentStrategy {

    // Taux de conversion fictif EUR → USDC
    private static final BigDecimal EUR_TO_USDC = new BigDecimal("1.08");

    @Override
    public String getMethodName() {
        return "CRYPTO";
    }

    @Override
    public PaymentResult process(BigDecimal amount, String userId, String details) {
        var amountUsdc = amount.multiply(EUR_TO_USDC).setScale(2, RoundingMode.HALF_UP);
        log.info("[Crypto] Création charge {} USDC ({} EUR) pour user {}", amountUsdc, amount, userId);

        // Simulation création d'une charge Coinbase Commerce
        var chargeId = "charge_" + UUID.randomUUID().toString().substring(0, 16);
        log.info("[Crypto] Charge créée – id: {} – wallet: {}", chargeId, details);
        return PaymentResult.ok(chargeId);
    }

    @Override
    public boolean refund(String providerRef, BigDecimal amount) {
        // Les transactions crypto sont irréversibles on-chain
        // En pratique : remboursement en stablecoin via l'API Coinbase
        log.warn("[Crypto] Remboursement crypto initié manuellement – ref: {}", providerRef);
        return true;
    }
}
