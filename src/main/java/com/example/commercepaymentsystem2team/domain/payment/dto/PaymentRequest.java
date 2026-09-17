package com.example.commercepaymentsystem2team.domain.payment.dto;

import com.example.commercepaymentsystem2team.domain.payment.entity.PaymentResult;

public record PaymentRequest(
        Long orderId,
        PaymentResult result,
        int amount
) {
}