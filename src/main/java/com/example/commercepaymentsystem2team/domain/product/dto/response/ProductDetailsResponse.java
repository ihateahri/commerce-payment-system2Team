package com.example.commercepaymentsystem2team.domain.product.dto.response;

import com.example.commercepaymentsystem2team.domain.product.entity.ProductCategory;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductStatus;

import java.time.LocalDateTime;

public record ProductDetailsResponse(
        Long id,
        String name,
        Long price,
        Integer stock,
        String description,
        ProductStatus status,
        ProductCategory category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
