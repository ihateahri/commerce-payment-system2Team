package com.example.commercepaymentsystem2team.domain.refund.controller;

import com.example.commercepaymentsystem2team.common.response.ApiResponse;
import com.example.commercepaymentsystem2team.domain.refund.dto.request.RefundRequest;
import com.example.commercepaymentsystem2team.domain.refund.dto.response.RefundResponse;
import com.example.commercepaymentsystem2team.domain.refund.facade.RefundFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundFacade refundFacade; // Service → Facade로 교체

    @PostMapping
    public ResponseEntity<ApiResponse<RefundResponse>> cancel(
            @Valid @RequestBody RefundRequest request,
            @AuthenticationPrincipal Long memberId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(refundFacade.cancel(request, memberId)));
    }
}
