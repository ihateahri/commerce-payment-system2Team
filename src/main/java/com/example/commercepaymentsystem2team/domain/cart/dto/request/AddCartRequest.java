package com.example.commercepaymentsystem2team.domain.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartRequest(
        @NotNull(message = "상품 id 필수")Long productId,
        @Min(value = 1,message = "수량은 1 이상") Integer quantity
) {

}
