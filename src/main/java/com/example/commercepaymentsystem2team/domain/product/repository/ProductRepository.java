package com.example.commercepaymentsystem2team.domain.product.repository;

import com.example.commercepaymentsystem2team.domain.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity,Long> {
}
