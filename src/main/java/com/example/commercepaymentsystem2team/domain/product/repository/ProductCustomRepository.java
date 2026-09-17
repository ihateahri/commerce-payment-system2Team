package com.example.commercepaymentsystem2team.domain.product.repository;

import com.example.commercepaymentsystem2team.domain.product.entity.Product;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductCategory;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductCustomRepository {

    Page<Product> findAllByCondition(
            ProductCategory category,
            Long minPrice,
            Long maxPrice,
            List<ProductStatus> statuses,
            String sort,
            Pageable pageable
    );
}

