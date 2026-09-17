package com.example.commercepaymentsystem2team.domain.product.dto.response;

import com.example.commercepaymentsystem2team.domain.product.entity.ProductCategory;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductStatus;

import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        Long price,
        ProductCategory category,
        ProductStatus status,
        LocalDateTime createdAt
) {
}
