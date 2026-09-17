package com.example.commercepaymentsystem2team.domain.refund.facade;

import com.example.commercepaymentsystem2team.domain.order.entity.Order;
import com.example.commercepaymentsystem2team.domain.order.service.OrderService;
import com.example.commercepaymentsystem2team.domain.payment.entity.Payment;
import com.example.commercepaymentsystem2team.domain.payment.service.PaymentService;
import com.example.commercepaymentsystem2team.domain.refund.dto.request.RefundRequest;
import com.example.commercepaymentsystem2team.domain.refund.dto.response.RefundResponse;
import com.example.commercepaymentsystem2team.domain.refund.entity.Refund;
import com.example.commercepaymentsystem2team.domain.refund.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefundFacade {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final RefundService refundService;

    @Transactional
    public RefundResponse cancel(RefundRequest request, Long memberId) {

        Order order = orderService.getCancelableOrder(request.orderId(), memberId);
        Payment payment = paymentService.findByOrderIdAndMemberId(order.getId(), memberId);

        Refund refund = null;
        boolean wasPaid = payment.isPaid();

        paymentService.cancelPayment(payment);
        if (wasPaid) {
            refund = refundService.create(payment, request.reason());
        }
        orderService.cancel(order, request.reason());

        return RefundResponse.of(order, payment, refund, request.reason());
    }
}
