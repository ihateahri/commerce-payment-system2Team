package com.example.commercepaymentsystem2team.domain.cart.entity;

import com.example.commercepaymentsystem2team.common.entity.BaseEntity;
import com.example.commercepaymentsystem2team.common.exception.BusinessException;
import com.example.commercepaymentsystem2team.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.example.commercepaymentsystem2team.common.exception.ErrorCode.INVALID_QUANTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Cart_Items", uniqueConstraints = {@UniqueConstraint(columnNames = {"cart_id", "product_id"})})
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, columnDefinition = "int UNSIGNED DEFAULT 1")
    private Integer quantity;

    public CartItem(Cart cart, Product product, Integer quantity) {
        this.cart = cart;
        this.product = product;
        if (quantity < 1) {
            throw new BusinessException(INVALID_QUANTITY);
        }
        this.quantity = quantity;
    }

    public Long getProductId() {
        return product.getId();
    }

    public Long getMemberId() {
        return cart.getMemberId();
    }

    //장바구니 상품 담을시
    public void addQuantity(Integer quantity) {
        int newQuantity = this.quantity + quantity;
        if (newQuantity < 1) {
            throw new BusinessException(INVALID_QUANTITY);
        }
        this.quantity = newQuantity;
    }

    //장바구니 상품 수량 수정시
    public void changeQuantity(Integer quantity) {
        if (quantity < 1) {
            throw new BusinessException(INVALID_QUANTITY);
        }
        this.quantity = quantity;
    }
}
