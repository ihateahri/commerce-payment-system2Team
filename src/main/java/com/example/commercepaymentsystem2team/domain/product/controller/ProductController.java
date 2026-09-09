package com.example.commercepaymentsystem2team.domain.product.controller;

import com.example.commercepaymentsystem2team.domain.product.dto.request.UpdateRequest;
import com.example.commercepaymentsystem2team.domain.product.dto.response.ProductDetailsResponse;
import com.example.commercepaymentsystem2team.domain.product.dto.response.ProductResponse;
import com.example.commercepaymentsystem2team.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping //전체조회
    public ResponseEntity<List<ProductResponse>> findAll(){
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}") //단권 조회
    public ResponseEntity<ProductDetailsResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(productService.findById(id));
    }

    @PatchMapping("/{id}") //수정
    public ResponseEntity<ProductDetailsResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateRequest request){
        return ResponseEntity.ok(productService.update(id,request));
    }



}
