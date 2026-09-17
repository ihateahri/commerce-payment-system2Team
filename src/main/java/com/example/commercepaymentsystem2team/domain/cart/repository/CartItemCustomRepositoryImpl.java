package com.example.commercepaymentsystem2team.domain.cart.repository;

import com.example.commercepaymentsystem2team.domain.cart.entity.CartItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.commercepaymentsystem2team.domain.cart.entity.QCartItem.cartItem;
import static com.example.commercepaymentsystem2team.domain.product.entity.QProduct.product;

@RequiredArgsConstructor
public class CartItemCustomRepositoryImpl implements CartItemCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CartItem> findAllByMemberId(Long memberId) {
        return queryFactory
                .selectFrom(cartItem)
                .join(cartItem.product, product)
                .fetchJoin()
                .where(cartItem.cart.member.id.eq(memberId))
                .fetch();
    }
}
