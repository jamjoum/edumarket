package com.edumarket.factory;

import com.edumarket.model.Payment;
import com.edumarket.model.Subscription;
import com.edumarket.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * ┌─────────────────────────────────────────────────────────────┐
 * │              DESIGN PATTERN : FACTORY                       │
 * ├─────────────────────────────────────────────────────────────┤
 * │  Problème : la création d'un Subscription implique          │
 * │  plusieurs règles métier (durée, dates, statut) qui ne      │
 * │  doivent pas polluer le service appelant.                   │
 * │                                                             │
 * │  Solution : centraliser la logique de construction dans     │
 * │  une Factory dédiée. Le service ne connaît que le plan      │
 * │  choisi et obtient un objet prêt à persister.               │
 * └─────────────────────────────────────────────────────────────┘
 */
@Component
public class SubscriptionFactory {

    @Value("${edumarket.subscription.trial-days:14}")
    private int trialDays;

    /**
     * Fabrique un Subscription selon le plan choisi.
     *
     * Java 21 Switch Expression avec Arrow Syntax.
     */
    public Subscription create(User user, Subscription.Plan plan, Payment payment) {
        var now = LocalDateTime.now();

        // ▼ Switch Expression Java 21 ▼
        LocalDateTime expiresAt = switch (plan) {
            case MONTHLY -> now.plusMonths(1);
            case YEARLY  -> now.plusYears(1);
            case TRIAL   -> now.plusDays(trialDays);
        };

        return Subscription.builder()
            .user(user)
            .plan(plan)
            .status(Subscription.SubscriptionStatus.ACTIVE)
            .startedAt(now)
            .expiresAt(expiresAt)
            .payment(payment)
            .build();
    }

    /**
     * Renouvellement d'un abonnement existant.
     */
    public Subscription renew(Subscription existing) {
        var now = LocalDateTime.now();
        // On part de la date d'expiration si l'abo est encore valide, sinon de maintenant
        var base = existing.getExpiresAt().isAfter(now) ? existing.getExpiresAt() : now;

        LocalDateTime newExpiry = switch (existing.getPlan()) {
            case MONTHLY -> base.plusMonths(1);
            case YEARLY  -> base.plusYears(1);
            case TRIAL   -> base.plusDays(trialDays);
        };

        existing.setExpiresAt(newExpiry);
        existing.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        return existing;
    }
}
