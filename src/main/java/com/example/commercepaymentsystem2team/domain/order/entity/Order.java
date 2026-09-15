package com.example.commercepaymentsystem2team.domain.order.entity;

import com.example.commercepaymentsystem2team.domain.member.entity.Member;
import com.example.commercepaymentsystem2team.common.entity.BaseEntity;
import com.example.commercepaymentsystem2team.common.exception.BusinessException;
import com.example.commercepaymentsystem2team.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private String cancelReason;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    private Order(
            Member member,
            String orderNumber,
            Long totalAmount
    ) {
        this.member = member;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;

        this.status = OrderStatus.PENDING_PAYMENT;
    }

    public static Order create(
            Member member,
            String orderNumber,
            Long totalAmount
    ) {
        return new Order(
                member,
                orderNumber,
                totalAmount
        );
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel(String reason) {

        if (this.status == OrderStatus.CANCELED) {
            throw new BusinessException(ErrorCode.ALREADY_CANCELED);
        }

        this.status = OrderStatus.CANCELED;
        this.cancelReason = reason;
    }
    public void cancel() {
        this.status = OrderStatus.CANCELED;
    }

    public boolean isOwnedBy(Long memberId) {
        return this.member.getId().equals(memberId);
    }
}
