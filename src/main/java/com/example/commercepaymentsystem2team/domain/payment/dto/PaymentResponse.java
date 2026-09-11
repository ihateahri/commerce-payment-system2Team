package com.example.commercepaymentsystem2team.domain.payment.dto;

import com.example.commercepaymentsystem2team.domain.order.entity.OrderStatus;
import com.example.commercepaymentsystem2team.domain.payment.entity.PaymentStatus;

public record PaymentResponse(
        PaymentStatus paymentStatus,
        OrderStatus orderStatus
) {
}