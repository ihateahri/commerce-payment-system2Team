package com.example.commercepaymentsystem2team.domain.order.controller;

import com.example.commercepaymentsystem2team.common.response.ApiResponse;
import com.example.commercepaymentsystem2team.domain.order.dto.request.OrderCreateRequest;
import com.example.commercepaymentsystem2team.domain.order.dto.response.OrderCreateResponse;
import com.example.commercepaymentsystem2team.domain.order.dto.response.OrderDetailResponse;
import com.example.commercepaymentsystem2team.domain.order.dto.response.OrderPageResponse;
import com.example.commercepaymentsystem2team.domain.order.dto.response.OrderPreviewResponse;
import com.example.commercepaymentsystem2team.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // =========================
    // 주문 생성
    // =========================
    @PostMapping
    public ResponseEntity<ApiResponse<OrderCreateResponse>> createOrder(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody OrderCreateRequest request
    ) {
        OrderCreateResponse response = orderService.createOrder(memberId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }


    // =========================
    // 주문 목록 조회
    // =========================
    @GetMapping
    public ResponseEntity<ApiResponse<OrderPageResponse>> getOrderList(
            @AuthenticationPrincipal Long memberId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        OrderPageResponse response = orderService.getOrderList(memberId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }


    // =========================
    // 주문 상세 조회
    // =========================
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetail(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long orderId
    ) {
        OrderDetailResponse response = orderService.getOrderDetail(memberId, orderId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }


    // =========================
    // 주문서 미리보기
    // =========================
    @PostMapping("/preview")
    public ResponseEntity<ApiResponse<OrderPreviewResponse>> previewOrder(
            @Valid @RequestBody OrderCreateRequest request
    ) {
        OrderPreviewResponse response = orderService.previewOrder(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}