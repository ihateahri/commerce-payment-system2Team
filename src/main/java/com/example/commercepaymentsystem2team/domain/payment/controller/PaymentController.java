package com.example.commercepaymentsystem2team.domain.payment.controller;

import com.example.commercepaymentsystem2team.common.response.ApiResponse;
import com.example.commercepaymentsystem2team.domain.payment.dto.PaymentDetailResponse;
import com.example.commercepaymentsystem2team.domain.payment.dto.PaymentRequest;
import com.example.commercepaymentsystem2team.domain.payment.dto.PaymentResponse;
import com.example.commercepaymentsystem2team.domain.payment.service.PaymentCommandService;
import com.example.commercepaymentsystem2team.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentCommandService paymentCommandService;
    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @AuthenticationPrincipal Long memberId,
            @RequestBody PaymentRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(paymentCommandService.tryPayment(request, memberId)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<PaymentDetailResponse>> getPayment(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPayment(orderId, memberId)));
    }
}