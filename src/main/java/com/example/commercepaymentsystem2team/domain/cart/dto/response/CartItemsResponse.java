package com.example.commercepaymentsystem2team.domain.cart.dto.response;

import com.example.commercepaymentsystem2team.domain.product.entity.ProductStatus;

import java.time.LocalDateTime;

public record CartItemsResponse(
        Long cartItemsId,
        Long productId,
        String name,
        Long price,
        Integer quantity,
        ProductStatus status,
        LocalDateTime createdAt,
        Integer stock

) {
}
