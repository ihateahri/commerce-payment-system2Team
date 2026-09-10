package com.example.commercepaymentsystem2team.domain.product.service;

import com.example.commercepaymentsystem2team.common.exception.BusinessException;
import com.example.commercepaymentsystem2team.common.exception.ErrorCode;
import com.example.commercepaymentsystem2team.domain.product.dto.request.UpdateRequest;
import com.example.commercepaymentsystem2team.domain.product.dto.response.PageResponse;
import com.example.commercepaymentsystem2team.domain.product.dto.response.ProductDetailsResponse;
import com.example.commercepaymentsystem2team.domain.product.dto.response.ProductResponse;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductCategory;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductEntity;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductStatus;
import com.example.commercepaymentsystem2team.domain.product.repository.ProductRepository;
import com.example.commercepaymentsystem2team.domain.product.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    private static final List<ProductStatus> VISIBLE_STATUSES= List.of(ProductStatus.ON_SALE,ProductStatus.SOLD_OUT);

    //전체 조회
    public PageResponse<ProductResponse> findAll(int page, int size, ProductCategory category, Long minPrice, Long maxPrice, String sort) {

        validatePriceRange(minPrice,maxPrice);

        Pageable pageable = PageRequest.of(page, size, resolveSort(sort));

        Specification<ProductEntity> spec = Specification.where(ProductSpecification.statusIn(VISIBLE_STATUSES))
                .and(ProductSpecification.hasCategory(category))
                .and(ProductSpecification.priceGte(minPrice))
                .and(ProductSpecification.priceLte(maxPrice));

        Page<ProductEntity> result=productRepository.findAll(spec,pageable);

        List<ProductResponse> content= result.getContent().stream()
                .map(this::toList)
                .toList();
        return PageResponse.of(content,page,size,result.getTotalElements());
    }

    private void validatePriceRange(Long minPrice, Long maxPrice){
        if (minPrice != null && minPrice < 0){
            throw new BusinessException(ErrorCode.INVALID_PRICE);
        }
        if (maxPrice != null && maxPrice < 0){
            throw new BusinessException(ErrorCode.INVALID_PRICE);
        }
        if (minPrice != null && maxPrice != null && minPrice > maxPrice){
            throw new BusinessException(ErrorCode.INVALID_PRICE_RANGE);
        }
    }

    private Sort resolveSort(String sort){
        if (sort==null || sort.equals("LATEST")){
            return Sort.by("createdAt").descending();
        }
        return switch (sort){
            case "PRICE_ASC" ->Sort.by("price").ascending();
            case "PRICE_DESC" ->Sort.by("price").descending();
            default -> throw new BusinessException(ErrorCode.INVALID_SORT);
        };
    }

    //단건 조회
    public ProductDetailsResponse findById(Long id){
        ProductEntity productEntity = findProductEntity(id);
        return toDetails(productEntity);
    }

    //수정
    @Transactional
    public ProductDetailsResponse update(Long id, UpdateRequest request){
        ProductEntity productEntity = findProductEntity(id);
        productEntity.update(
                request.name(),
                request.price(),
                request.description(),
                request.category());
        return toDetails(productEntity);

    }

    public ProductEntity findProductEntity(Long id){
        return productRepository.findById(id).orElseThrow(()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    } // 추후 다른 도메인에서 상품 엔티티 사용할때

    public ProductResponse toList(ProductEntity productEntity){
        return new ProductResponse(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getPrice(),
                productEntity.getCategory(),
                productEntity.getStatus(),
                productEntity.getCreatedAt()
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
                productEntity.getCategory(),
                productEntity.getCreatedAt(),
                productEntity.getUpdatedAt()
        );
    }

}
