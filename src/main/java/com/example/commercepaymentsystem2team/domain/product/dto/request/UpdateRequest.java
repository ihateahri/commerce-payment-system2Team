package com.example.commercepaymentsystem2team.domain.product.dto.request;

import com.example.commercepaymentsystem2team.domain.product.entity.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record UpdateRequest(
       @Size(max = 100,message = "이름은 100글자 입력이 가능합니다.")
       String name,
       @NotNull(message = "상품 가격 필수 입력")
       Long price,
       String description,
       ProductCategory category
) {

}
