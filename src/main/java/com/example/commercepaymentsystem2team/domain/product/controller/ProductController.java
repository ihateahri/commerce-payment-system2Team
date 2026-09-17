package com.example.commercepaymentsystem2team.domain.product.controller;

import com.example.commercepaymentsystem2team.domain.product.dto.request.UpdateRequest;
import com.example.commercepaymentsystem2team.domain.product.dto.response.PageResponse;
import com.example.commercepaymentsystem2team.domain.product.dto.response.ProductDetailsResponse;
import com.example.commercepaymentsystem2team.domain.product.dto.response.ProductResponse;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductCategory;
import com.example.commercepaymentsystem2team.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping //전체조회
    public ResponseEntity<PageResponse<ProductResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(defaultValue = "LATEST") String sort
    ) {
        return ResponseEntity.ok(productService.findAll(page, size, category, minPrice, maxPrice, sort));
    }

    @GetMapping("/{id}") //단권 조회
    public ResponseEntity<ProductDetailsResponse> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PatchMapping("/{id}") //수정
    public ResponseEntity<ProductDetailsResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRequest request
    ) {
        return ResponseEntity.ok(productService.update(id, request));
    }


}
