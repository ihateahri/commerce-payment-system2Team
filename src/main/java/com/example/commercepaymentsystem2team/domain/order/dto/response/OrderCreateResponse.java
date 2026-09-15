package com.example.commercepaymentsystem2team.domain.order.dto.response;

import com.example.commercepaymentsystem2team.domain.order.entity.OrderStatus;

public record OrderCreateResponse(
        Long orderId,
        String orderNumber,
        Long totalAmount,
        OrderStatus status
) {
}