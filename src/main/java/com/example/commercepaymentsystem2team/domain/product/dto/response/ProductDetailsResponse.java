package com.example.commercepaymentsystem2team.domain.product.dto.response;

import com.example.commercepaymentsystem2team.domain.product.entity.ProductCategory;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductStatus;

public record ProductDetailsResponse(
        Long id,
        String name,
        Long price,
        Integer stock,
        String description,
        ProductStatus status,
        ProductCategory category

) {

}
