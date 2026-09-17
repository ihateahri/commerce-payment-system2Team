package com.example.commercepaymentsystem2team.domain.order.dto.response;

import com.example.commercepaymentsystem2team.domain.order.entity.Order;
import com.example.commercepaymentsystem2team.domain.order.entity.OrderItem;
import com.example.commercepaymentsystem2team.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
        Long orderId,
        String orderNumber,
        Long totalAmount,
        OrderStatus status,
        String cancelReason,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
    public static OrderDetailResponse from(Order order) {
        List<OrderItemResponse> items =
                order.getOrderItems()
                        .stream()
                        .map(OrderItemResponse::from)
                        .toList();

        return new OrderDetailResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCancelReason(),
                order.getCreatedAt(),
                items
        );
    }

    public record OrderItemResponse(
            Long productId,
            String productName,
            int quantity,
            Long orderPrice,
            Long totalPrice
    ) {
        public static OrderItemResponse from(OrderItem orderItem) {

            return new OrderItemResponse(
                    orderItem.getProduct().getId(),
                    orderItem.getProduct().getName(),
                    orderItem.getQuantity(),
                    orderItem.getOrderPrice(),
                    orderItem.getTotalPrice()
            );
        }
    }
}
