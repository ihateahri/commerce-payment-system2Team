package com.example.commercepaymentsystem2team.domain.order.entity;

import com.example.commercepaymentsystem2team.domain.product.entity.Product;;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private Long orderPrice;

    private OrderItem(
            Product product,
            int quantity,
            Long orderPrice
    ) {
        this.product = product;
        this.quantity = quantity;
        this.orderPrice = orderPrice;
    }

    public static OrderItem create(
            Product product,
            int quantity,
            Long orderPrice
    ) {
        return new OrderItem(product, quantity, orderPrice);
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Long getTotalPrice() {
        return orderPrice * quantity;
    }
}