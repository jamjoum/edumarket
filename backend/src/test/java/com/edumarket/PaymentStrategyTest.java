package com.edumarket.payment;

import com.edumarket.model.Payment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests du Pattern Strategy – PaymentContext + Stratégies.
 */
@DisplayName("Strategy Pattern – Paiement")
class PaymentStrategyTest {

    private final PaymentContext context = new PaymentContext(
        new CreditCardPaymentStrategy(),
        new PayPalPaymentStrategy(),
        new CryptoPaymentStrategy()
    );

    @ParameterizedTest
    @EnumSource(Payment.PaymentMethod.class)
    @DisplayName("Chaque méthode de paiement résout une stratégie non-null")
    void resolve_returnsStrategyForEveryMethod(Payment.PaymentMethod method) {
        var strategy = context.resolve(method);
        assertThat(strategy).isNotNull();
        assertThat(strategy.getMethodName()).isEqualTo(method.name());
    }

    @Test
    @DisplayName("CreditCard – paiement simulé réussit")
    void creditCard_processSucceeds() {
        var strategy = new CreditCardPaymentStrategy();
        var result   = strategy.process(BigDecimal.valueOf(49.99), "user-123", null);
        assertThat(result.success()).isTrue();
        assertThat(result.providerRef()).startsWith("ch_");
    }

    @Test
    @DisplayName("PayPal – paiement échoue sans email")
    void paypal_failsWithoutEmail() {
        var strategy = new PayPalPaymentStrategy();
        var result   = strategy.process(BigDecimal.valueOf(39.99), "user-456", "");
        assertThat(result.success()).isFalse();
    }

    @Test
    @DisplayName("PayPal – paiement réussit avec email")
    void paypal_succeedsWithEmail() {
        var strategy = new PayPalPaymentStrategy();
        var result   = strategy.process(BigDecimal.valueOf(39.99), "user-456", "user@paypal.com");
        assertThat(result.success()).isTrue();
        assertThat(result.providerRef()).startsWith("PAYPAL-");
    }

    @Test
    @DisplayName("Crypto – retourne une référence de charge")
    void crypto_processSucceeds() {
        var strategy = new CryptoPaymentStrategy();
        var result   = strategy.process(BigDecimal.valueOf(79.99), "user-789", "0xABCDEF");
        assertThat(result.success()).isTrue();
        assertThat(result.providerRef()).startsWith("charge_");
    }
}
