package com.example.commercepaymentsystem2team.domain.order.dto.response;

import java.util.List;

public record OrderPreviewResponse(
        List<OrderPreviewItemResponse> items,
        Long totalAmount
) {
    public record OrderPreviewItemResponse(
            Long productId,
            String productName,
            Long price,
            int quantity,
            Long totalPrice
    ) {
    }
}