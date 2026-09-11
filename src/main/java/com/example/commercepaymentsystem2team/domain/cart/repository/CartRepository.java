package com.example.commercepaymentsystem2team.domain.cart.repository;

import com.example.commercepaymentsystem2team.domain.cart.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity,Long> {
    Optional<CartEntity> findByMember_Id(Long memberId);

}
