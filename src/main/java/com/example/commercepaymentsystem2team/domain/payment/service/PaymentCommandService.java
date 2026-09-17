package com.example.commercepaymentsystem2team.domain.payment.service;

import com.example.commercepaymentsystem2team.common.exception.BusinessException;
import com.example.commercepaymentsystem2team.common.exception.ErrorCode;
import com.example.commercepaymentsystem2team.domain.cart.service.CartService;
import com.example.commercepaymentsystem2team.domain.order.entity.Order;
import com.example.commercepaymentsystem2team.domain.order.entity.OrderStatus;
import com.example.commercepaymentsystem2team.domain.order.service.OrderService;
import com.example.commercepaymentsystem2team.domain.payment.dto.PaymentRequest;
import com.example.commercepaymentsystem2team.domain.payment.dto.PaymentResponse;
import com.example.commercepaymentsystem2team.domain.payment.entity.Payment;
import com.example.commercepaymentsystem2team.domain.payment.entity.PaymentResult;
import com.example.commercepaymentsystem2team.domain.payment.entity.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentCommandService {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final CartService cartService;

    @Transactional
    public PaymentResponse tryPayment(
            PaymentRequest request,
            Long memberId
    ) {
        // 1. 본인 소유 주문인지 확인
        Order order = orderService.findByIdAndMemberId(
                request.orderId(),
                memberId
        );

        // 2. 주문에 해당하는 결제 조회
        Payment payment =
                paymentService.findByOrderIdAndMemberId(
                        request.orderId(),
                        memberId
                );

        // 3. 결제 상태 확인
        if (payment.getStatus() != PaymentStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);
        }

        // 4. 주문 상태 확인
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS);
        }

        // 5. 결제 금액 검증
        if (request.amount() != payment.getAmount()) {
            throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 6. 결제 성공
        if (request.result() == PaymentResult.SUCCESS) {
            // 결제 완료
            paymentService.confirmPayment(payment);
            // 주문 완료
            orderService.confirmOrder(order);
            // 장바구니 비우기
            cartService.clearCart(memberId);
        }

        // 7. 결제 실패
        if (request.result() == PaymentResult.FAIL) {
            // 결제 실패
            paymentService.failPayment(payment);
            // 주문 취소 + 재고 복구
            orderService.cancel(order, "결제 실패");
        }

        return new PaymentResponse(
                payment.getStatus(),
                order.getStatus()
        );
    }
}