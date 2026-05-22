package com.edumarket.service;

import com.edumarket.dto.PaymentDTO;
import com.edumarket.exception.ResourceNotFoundException;
import com.edumarket.factory.SubscriptionFactory;
import com.edumarket.model.Payment;
import com.edumarket.model.Subscription;
import com.edumarket.payment.PaymentContext;
import com.edumarket.repository.CourseRepository;
import com.edumarket.repository.PaymentRepository;
import com.edumarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestre les paiements (cours à l'unité ou abonnement).
 *
 * Délègue à PaymentContext (Strategy) pour le traitement réel.
 * Délègue à SubscriptionFactory (Factory) pour la création d'abonnement.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository    paymentRepository;
    private final UserRepository       userRepository;
    private final CourseRepository     courseRepository;
    private final PaymentContext       paymentContext;
    private final SubscriptionFactory  subscriptionFactory;

    /**
     * Traite un paiement de cours à l'unité.
     *
     * Java 21 – Virtual Threads : l'appel HTTP entrant arrive déjà
     * sur un Virtual Thread; pas besoin de CompletableFuture ici.
     */
    @Transactional
    public PaymentDTO.Response payForCourse(PaymentDTO.Request request) {
        var user   = userRepository.findById(request.userId())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        var course = courseRepository.findById(request.courseId())
            .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));

        // ► Strategy Pattern : le contexte choisit la bonne implémentation
        var result = paymentContext.execute(
            request.paymentMethod(),
            course.getPrice(),
            user.getId().toString(),
            null
        );

        // ▼ Switch Expression Java 21 ▼
        var status = switch (result.success() ? "ok" : "ko") {
            case "ok" -> Payment.PaymentStatus.SUCCESS;
            default   -> Payment.PaymentStatus.FAILED;
        };

        var payment = Payment.builder()
            .user(user)
            .course(course)
            .amount(course.getPrice())
            .paymentMethod(request.paymentMethod())
            .status(status)
            .providerRef(result.providerRef())
            .build();

        var saved = paymentRepository.save(payment);
        log.info("Paiement {} pour cours {} – status: {}", saved.getId(), course.getTitle(), status);
        return toResponse(saved);
    }

    /**
     * Traite un paiement d'abonnement et crée la Subscription.
     */
    @Transactional
    public PaymentDTO.Response payForSubscription(PaymentDTO.Request request) {
        var user = userRepository.findById(request.userId())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        var plan = Subscription.Plan.valueOf(
            request.planType() != null ? request.planType().toUpperCase() : "MONTHLY"
        );

        // Calcul prix selon le plan
        var amount = switch (plan) {
            case MONTHLY -> java.math.BigDecimal.valueOf(19.99);
            case YEARLY  -> java.math.BigDecimal.valueOf(149.99);
            case TRIAL   -> java.math.BigDecimal.ZERO;
        };

        var result = paymentContext.execute(
            request.paymentMethod(), amount, user.getId().toString(), null
        );

        var payment = paymentRepository.save(Payment.builder()
            .user(user)
            .amount(amount)
            .paymentMethod(request.paymentMethod())
            .status(result.success() ? Payment.PaymentStatus.SUCCESS : Payment.PaymentStatus.FAILED)
            .providerRef(result.providerRef())
            .build());

        if (result.success()) {
            // ► Factory Pattern : délègue la construction de la Subscription
            var subscription = subscriptionFactory.create(user, plan, payment);
            user.setPremium(true);
            userRepository.save(user);
            log.info("Abonnement {} créé pour {}", plan, user.getEmail());
        }

        return toResponse(payment);
    }

    private PaymentDTO.Response toResponse(Payment p) {
        return new PaymentDTO.Response(
            p.getId(), p.getAmount(), p.getCurrency(),
            p.getPaymentMethod(), p.getStatus(),
            p.getProviderRef(), p.getCreatedAt()
        );
    }
}
