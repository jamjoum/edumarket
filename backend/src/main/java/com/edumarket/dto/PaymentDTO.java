package com.edumarket.dto;

import com.edumarket.model.Payment;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public final class PaymentDTO {

    public record Request(
        @NotNull UUID                    userId,
        UUID                             courseId,      // null si abonnement
        @NotNull Payment.PaymentMethod   paymentMethod,
        String                           planType       // MONTHLY | YEARLY | null
    ) {}

    public record Response(
        UUID                    id,
        BigDecimal              amount,
        String                  currency,
        Payment.PaymentMethod   paymentMethod,
        Payment.PaymentStatus   status,
        String                  providerRef,
        LocalDateTime           createdAt
    ) {}
}
