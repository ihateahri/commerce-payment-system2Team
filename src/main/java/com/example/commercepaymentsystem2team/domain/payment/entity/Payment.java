package com.example.commercepaymentsystem2team.domain.payment.entity;

import com.example.commercepaymentsystem2team.common.entity.BaseEntity;
import com.example.commercepaymentsystem2team.common.exception.BusinessException;
import com.example.commercepaymentsystem2team.common.exception.ErrorCode;
import com.example.commercepaymentsystem2team.domain.member.entity.Member;
import com.example.commercepaymentsystem2team.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status = PaymentStatus.IN_PROGRESS;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    public Payment(Order order, int amount, Member member) {
        this.order = order;
        this.amount = amount;
        this.member = member;
    }

    public void markAsPaid() {
        changeStatus(PaymentStatus.PAID);
        this.paidAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        changeStatus(PaymentStatus.FAILED);
    }

    public void markAsCancelled() {
        changeStatus(PaymentStatus.CANCELLED);
    }

    public void changeStatus(PaymentStatus status) {
        if (!this.status.canTransitTo(status)) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);
        }
        this.status = status;
    }

    public boolean isPaid() {
        return this.status == PaymentStatus.PAID;
    }
}