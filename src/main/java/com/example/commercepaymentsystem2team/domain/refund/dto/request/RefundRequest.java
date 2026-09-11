package com.example.commercepaymentsystem2team.domain.refund.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RefundRequest(
        @NotNull
        Long orderId,

        @NotBlank
        @Size(max = 100)
        String reason
) {
}
