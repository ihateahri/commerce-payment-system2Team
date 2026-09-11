package com.example.commercepaymentsystem2team.domain.cart.dto.response;

import java.util.List;

public record CartResponse(
        List<CartItemsResponse> cartItemsResponseList,
        Long totalPrice
) {
}
