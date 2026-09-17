package com.example.commercepaymentsystem2team.domain.cart.repository;

import com.example.commercepaymentsystem2team.domain.cart.entity.CartItem;

import java.util.List;

public interface CartItemCustomRepository {

    List<CartItem> findAllByMemberId(Long memberId);
}
