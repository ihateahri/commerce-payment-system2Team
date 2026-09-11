package com.example.commercepaymentsystem2team.domain.cart.facade;

import com.example.commercepaymentsystem2team.domain.cart.dto.request.AddCartRequest;
import com.example.commercepaymentsystem2team.domain.cart.service.CartService;
import com.example.commercepaymentsystem2team.domain.member.entity.Member;
import com.example.commercepaymentsystem2team.domain.member.service.MemberService;
import com.example.commercepaymentsystem2team.domain.product.entity.Product;
import com.example.commercepaymentsystem2team.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CartFacade {

    private final CartService cartService;
    private final MemberService memberService;
    private final ProductService productService;

    @Transactional
    public Long addItem(Long memberId, AddCartRequest request){
        Member member = memberService.findMember(memberId);
        Product product = productService.findProductEntity(request.productId());
        return cartService.addItem(member, product,request.quantity());
    }
}
