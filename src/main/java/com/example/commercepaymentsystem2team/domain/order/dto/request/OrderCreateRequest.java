package com.example.commercepaymentsystem2team.domain.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OrderCreateRequest(

        @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다.")
        @Valid
        List<OrderItemRequest> items

) {

    public record OrderItemRequest(

            @NotNull(message = "상품 ID는 필수입니다.")
            Long productId,

            @Positive(message = "상품 수량은 1개 이상이어야 합니다.")
            int quantity

    ) {
    }
}