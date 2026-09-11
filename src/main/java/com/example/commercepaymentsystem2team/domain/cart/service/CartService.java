package com.example.commercepaymentsystem2team.domain.cart.service;

import com.example.commercepaymentsystem2team.common.exception.BusinessException;
import com.example.commercepaymentsystem2team.common.exception.ErrorCode;
import com.example.commercepaymentsystem2team.domain.cart.dto.response.CartItemsResponse;
import com.example.commercepaymentsystem2team.domain.cart.dto.response.CartResponse;
import com.example.commercepaymentsystem2team.domain.cart.entity.CartEntity;
import com.example.commercepaymentsystem2team.domain.cart.entity.CartItemsEntity;
import com.example.commercepaymentsystem2team.domain.cart.repository.CartItemsRepository;
import com.example.commercepaymentsystem2team.domain.cart.repository.CartRepository;
import com.example.commercepaymentsystem2team.domain.member.entity.Member;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductEntity;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.commercepaymentsystem2team.common.exception.ErrorCode.CART_ITEM_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartItemsRepository cartItemsRepository;
    private final CartRepository cartRepository;

    //장바구니 조회
    @Transactional(readOnly = true)
    public CartResponse getCartItems(Long memberId){
        List<CartItemsResponse> list = cartItemsRepository.findByMemberId(memberId).stream()
                .map(this::toResponse)
                .toList();
        Long totalPrice = list.stream()
                .mapToLong(item->item.price() * item.quantity())
                .sum();
                return new CartResponse(list,totalPrice);
    }

    //장바구니 상품 담기
    public Long addItem(Member member, ProductEntity product, Integer quantity){
        if (product.getStatus()== ProductStatus.SOLD_OUT || product.getStatus() == ProductStatus.DISCONTINUED){
            throw new BusinessException(ErrorCode.PRODUCT_NOT_AVAILABLE); //품절, 단종 상품
        }
        CartEntity cart = cartRepository.findByMember_Id(member.getId())
                .orElseGet(()->cartRepository.save(new CartEntity(member)));
        Optional<CartItemsEntity> existing=cartItemsRepository.findByCart_Member_IdAndProduct_Id(member.getId(), product.getId());

        if (existing.isPresent()){
            CartItemsEntity found=existing.get();
            int newQuantity = found.getQuantity() + quantity;
            if (newQuantity >product.getStock()){
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
            }
            found.addQuantity(quantity);
            return found.getId();
        }else {
            if (quantity>product.getStock()){
                throw  new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
            }
            CartItemsEntity cartItemsEntity = new CartItemsEntity(cart, product, quantity);
            return cartItemsRepository.save(cartItemsEntity).getId();
        }
    }

    //수량 변경
    public void updateQuantity(Long memberId,Long id,Integer quantity){
        CartItemsEntity cartItemsEntity=cartItemsRepository.findById(id)
                .filter(ci -> ci.getMemberId().equals(memberId))
                .orElseThrow(()->new BusinessException(CART_ITEM_NOT_FOUND));

        if (quantity > cartItemsEntity.getProduct().getStock()){
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }
        cartItemsEntity.changeQuantity(quantity);
    }

    //장바구니 개별 삭제
    public void removeItem(Long memberId,Long id){
        int deleted = cartItemsRepository.deleteByAndMember_Id(id,memberId);
        if (deleted==0){
            throw new BusinessException(CART_ITEM_NOT_FOUND);
        }
    }

    //장바구니 상품 전체 삭제
    public void clearCart(Long memberId){
        cartItemsRepository.deleteByAndMemberId(memberId);
    }

    //
    private CartItemsResponse toResponse(CartItemsEntity cartItemsEntity){
        return new CartItemsResponse(
                cartItemsEntity.getId(),
                cartItemsEntity.getProduct().getId(),
                cartItemsEntity.getProduct().getName(),
                cartItemsEntity.getProduct().getPrice(),
                cartItemsEntity.getQuantity(),
                cartItemsEntity.getProduct().getStatus(),
                cartItemsEntity.getCreatedAt(),
                cartItemsEntity.getProduct().getStock()
        );
    }

}
