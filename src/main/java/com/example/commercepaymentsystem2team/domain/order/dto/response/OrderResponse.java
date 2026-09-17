package com.example.commercepaymentsystem2team.domain.order.dto.response;

import com.example.commercepaymentsystem2team.domain.order.entity.Order;
import com.example.commercepaymentsystem2team.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;

public record OrderResponse(
        Long orderId,
        String orderNumber,
        Long totalAmount,
        OrderStatus status,
        LocalDateTime createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}