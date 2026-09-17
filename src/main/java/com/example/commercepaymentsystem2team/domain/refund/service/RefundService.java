package com.example.commercepaymentsystem2team.domain.refund.service;

import com.example.commercepaymentsystem2team.domain.payment.entity.Payment;
import com.example.commercepaymentsystem2team.domain.refund.entity.Refund;
import com.example.commercepaymentsystem2team.domain.refund.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;

    public Refund create(Payment payment, String reason) {
        return refundRepository.save(new Refund(payment, reason, payment.getAmount()));
    }
}
