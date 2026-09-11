package com.example.commercepaymentsystem2team.domain.payment.dto;

import com.example.commercepaymentsystem2team.domain.payment.entity.PaymentResult;
import com.example.commercepaymentsystem2team.domain.payment.entity.PaymentStatus;

public record PaymentRequest (
        Long orderId,
        PaymentResult result,
        int amount
) {
}