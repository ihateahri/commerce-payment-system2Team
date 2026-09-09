package com.example.commercepaymentsystem2team.domain.product.dto.response;

import com.example.commercepaymentsystem2team.domain.product.entity.ProductCategory;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductStatus;

public record ProductResponse(
        Long id,
        String name,
        Long price,
        ProductCategory category,
        ProductStatus status
) {

}
