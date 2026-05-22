package com.edumarket.controller;

import com.edumarket.dto.PaymentDTO;
import com.edumarket.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller des paiements.
 * Délègue à PaymentService qui utilise le Pattern Strategy.
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Paiements cours et abonnements")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/course")
    @Operation(summary = "Acheter un cours à l'unité (CB, PayPal, Crypto)")
    public ResponseEntity<PaymentDTO.Response> payForCourse(
        @Valid @RequestBody PaymentDTO.Request request
    ) {
        return ResponseEntity.ok(paymentService.payForCourse(request));
    }

    @PostMapping("/subscription")
    @Operation(summary = "Souscrire à un abonnement premium")
    public ResponseEntity<PaymentDTO.Response> payForSubscription(
        @Valid @RequestBody PaymentDTO.Request request
    ) {
        return ResponseEntity.ok(paymentService.payForSubscription(request));
    }
}
