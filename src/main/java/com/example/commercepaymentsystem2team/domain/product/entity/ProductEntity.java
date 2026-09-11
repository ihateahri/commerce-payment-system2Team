package com.example.commercepaymentsystem2team.domain.product.entity;

import com.example.commercepaymentsystem2team.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 100)

    private String name;
    @Column(nullable = false,columnDefinition = "BIGINT UNSIGNED")
    private Long price;

    @Column(nullable = false,columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer stock;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    public void update(String name, Long price, String description, ProductCategory category) {
        if (name != null) {
            this.name = name;
        }
        if (price != null) {
            if (price < 0) {
                throw new IllegalArgumentException("가격은 0이상이어야 합니다.");
            }
            this.price = price;
        }
        if (description != null) {
            this.description = description;
        }
        if (category != null) {
            this.category = category;
        }
    }

}
