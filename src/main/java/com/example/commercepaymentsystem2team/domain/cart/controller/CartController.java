package com.example.commercepaymentsystem2team.domain.cart.controller;

import com.example.commercepaymentsystem2team.domain.cart.dto.request.AddCartRequest;
import com.example.commercepaymentsystem2team.domain.cart.dto.request.UpdatedCartRequest;
import com.example.commercepaymentsystem2team.domain.cart.dto.response.AddCartResponse;
import com.example.commercepaymentsystem2team.domain.cart.dto.response.CartResponse;
import com.example.commercepaymentsystem2team.domain.cart.facade.CartFacade;
import com.example.commercepaymentsystem2team.domain.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
    private final CartFacade cartFacade;
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> findAll(@AuthenticationPrincipal Long memberId){
        return ResponseEntity.ok(cartService.getCartItems(memberId));
    }

    @PostMapping("/items")
    public ResponseEntity<AddCartResponse> addItem(@AuthenticationPrincipal Long memberId,
                                                   @Valid @RequestBody AddCartRequest request){
        Long cartItemId = cartFacade.addItem(memberId, request);
        return ResponseEntity.ok(new AddCartResponse(cartItemId,request.quantity()));
    }

    @PatchMapping("/items/{id}")
    public ResponseEntity<Void> updateQuanTity(@AuthenticationPrincipal Long memberId,
                                               @PathVariable Long id,
                                               @Valid @RequestBody UpdatedCartRequest request){
        cartService.updateQuantity(memberId,id,request.quantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeItem(@AuthenticationPrincipal Long memberId,@PathVariable Long id){
        cartService.removeItem(memberId, id);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal Long memberId){
        cartService.clearCart(memberId);
        return ResponseEntity.noContent().build();
    }
}
