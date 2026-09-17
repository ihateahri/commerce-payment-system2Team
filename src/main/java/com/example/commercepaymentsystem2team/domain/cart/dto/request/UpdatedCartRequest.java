package com.example.commercepaymentsystem2team.domain.cart.dto.request;

import jakarta.validation.constraints.Min;

public record UpdatedCartRequest(
        @Min(value = 1, message = "수량은 1 이상")
        Integer quantity
) {
}
