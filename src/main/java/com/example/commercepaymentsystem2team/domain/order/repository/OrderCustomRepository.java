package com.example.commercepaymentsystem2team.domain.order.repository;

import com.example.commercepaymentsystem2team.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderCustomRepository {
    Page<Order> findAllByMemberId(Long memberId, Pageable pageable);

    Optional<Order> findByIdAndMemberId(Long orderId, Long memberId);
}
