package com.example.commercepaymentsystem2team.domain.refund.dto.response;

import com.example.commercepaymentsystem2team.domain.order.entity.Order;
import com.example.commercepaymentsystem2team.domain.payment.entity.Payment;
import com.example.commercepaymentsystem2team.domain.refund.entity.Refund;

import java.time.LocalDateTime;

public record RefundResponse(
        Long orderId,
        String orderNumber,
        String orderStatus,
        String paymentStatus,
        Long refundId,
        Long refundAmount,
        LocalDateTime refundedAt,
        String reason
) {
    public static RefundResponse of(
            Order order,
            Payment payment,
            Refund refund,
            String reason
    ) {
        Long refundId = null;
        Long refundAmount = null;
        LocalDateTime refundedAt = null;

        if (refund != null) {
            refundId = refund.getId();
            refundAmount = refund.getAmount();
            refundedAt = refund.getCreatedAt();
        }

        return new RefundResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().name(),
                payment.getStatus().name(),
                refundId,
                refundAmount,
                refundedAt,
                reason
        );
    }
}
