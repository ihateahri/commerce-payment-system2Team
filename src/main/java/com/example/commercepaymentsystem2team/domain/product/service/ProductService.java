package com.example.commercepaymentsystem2team.domain.product.service;

import com.example.commercepaymentsystem2team.domain.product.dto.request.UpdateRequest;
import com.example.commercepaymentsystem2team.domain.product.dto.response.ProductDetailsResponse;
import com.example.commercepaymentsystem2team.domain.product.dto.response.ProductResponse;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductEntity;
import com.example.commercepaymentsystem2team.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    //전체 조회
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(this::toList)
                .toList();
    }

    //단건 조회
    public ProductDetailsResponse findById(Long id){
        ProductEntity productEntity = findProductEntity(id);
        return toDetails(productEntity);
    }

    //수정
    @Transactional
    public ProductDetailsResponse update(Long id,UpdateRequest request){
        ProductEntity productEntity = findProductEntity(id);
        productEntity.update(
                request.name(),
                request.price(),
                request.description(),
                request.category());
        return toDetails(productEntity);

    }

    public ProductEntity findProductEntity(Long id){
        return productRepository.findById(id).orElseThrow(()->new RuntimeException("상품을 찾을 수 없습니다"));
    } // 추후 다른 도메인에서 상품 엔티티 사용할때

    public ProductResponse toList(ProductEntity productEntity){
        return new ProductResponse(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getPrice(),
                productEntity.getCategory(),
                productEntity.getStatus()
        );
    }
    public ProductDetailsResponse toDetails(ProductEntity productEntity){
        return new ProductDetailsResponse(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getPrice(),
                productEntity.getStock(),
                productEntity.getDescription(),
                productEntity.getStatus(),
                productEntity.getCategory()
        );
    }

}
