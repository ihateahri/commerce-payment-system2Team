package com.example.commercepaymentsystem2team.domain.cart.entity;

import com.example.commercepaymentsystem2team.common.entity.BaseEntity;
import com.example.commercepaymentsystem2team.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Cart", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id"}
        )
})
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public Cart(Member member) {
        this.member = member;
    }

    public Long getMemberId() {
        return member.getId();
    }
}
