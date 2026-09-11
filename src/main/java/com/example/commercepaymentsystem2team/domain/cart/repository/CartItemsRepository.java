package com.example.commercepaymentsystem2team.domain.cart.repository;

import com.example.commercepaymentsystem2team.domain.cart.entity.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemsRepository extends JpaRepository<CartItems,Long> {

    @Query("SELECT ci FROM CartItems ci JOIN FETCH ci.product WHERE ci.cart.member.id= :memberId")
    List<CartItems> findByMemberId(@Param("memberId") Long memberId);

    Optional<CartItems> findByCart_Member_IdAndProduct_Id(Long memberId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItems ci WHERE ci.id = :id AND ci.cart.member.id = :memberId")
    int deleteByAndMember_Id(@Param("id") Long id,@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM CartItems ci WHERE ci.cart.member.id = :memberId")
    void deleteByAndMemberId(@Param("memberId") Long memberId);
}
