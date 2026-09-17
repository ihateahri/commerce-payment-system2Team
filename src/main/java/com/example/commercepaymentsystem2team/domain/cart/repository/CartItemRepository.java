package com.example.commercepaymentsystem2team.domain.cart.repository;

import com.example.commercepaymentsystem2team.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long>, CartItemCustomRepository {

    Optional<CartItem> findByCart_Member_IdAndProduct_Id(Long memberId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.id = :id AND ci.cart.member.id = :memberId")
    int deleteByAndMember_Id(@Param("id") Long id,@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.member.id = :memberId")
    void deleteByAndMemberId(@Param("memberId") Long memberId);
}
