package com.example.commercepaymentsystem2team.domain.cart.dto.response;

import java.util.List;

public record CartResponse(
        List<CartItemResponse> cartItemsResponseList,
        Long totalPrice
) {
}
