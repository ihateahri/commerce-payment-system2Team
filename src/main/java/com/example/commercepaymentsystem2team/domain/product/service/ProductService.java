package com.example.commercepaymentsystem2team.domain.product.service;

import com.example.commercepaymentsystem2team.common.exception.BusinessException;
import com.example.commercepaymentsystem2team.common.exception.ErrorCode;
import com.example.commercepaymentsystem2team.domain.product.dto.request.UpdateRequest;
import com.example.commercepaymentsystem2team.domain.product.dto.response.PageResponse;
import com.example.commercepaymentsystem2team.domain.product.dto.response.ProductDetailsResponse;
import com.example.commercepaymentsystem2team.domain.product.dto.response.ProductResponse;
import com.example.commercepaymentsystem2team.domain.product.entity.Product;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductCategory;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductStatus;
import com.example.commercepaymentsystem2team.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    private static final List<ProductStatus> VISIBLE_STATUSES = List.of(ProductStatus.ON_SALE, ProductStatus.SOLD_OUT);

    //전체 조회
    public PageResponse<ProductResponse> findAll(
            int page,
            int size,
            ProductCategory category,
            Long minPrice,
            Long maxPrice,
            String sort
    ) {
        validatePriceRange(minPrice, maxPrice);

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> result = productRepository.findAllByCondition(
                category,
                minPrice,
                maxPrice,
                VISIBLE_STATUSES,
                sort,
                pageable
        );
        List<ProductResponse> content = result.getContent().stream()
                .map(this::toList)
                .toList();
        return PageResponse.of(content, page, size, result.getTotalElements());
    }

    private void validatePriceRange(Long minPrice, Long maxPrice) {
        if (minPrice != null && minPrice < 0) {
            throw new BusinessException(ErrorCode.INVALID_PRICE);
        }
        if (maxPrice != null && maxPrice < 0) {
            throw new BusinessException(ErrorCode.INVALID_PRICE);
        }
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new BusinessException(ErrorCode.INVALID_PRICE_RANGE);
        }
    }

    private Sort resolveSort(String sort) {
        if (sort == null || sort.equals("LATEST")) {
            return Sort.by("createdAt").descending();
        }
        return switch (sort) {
            case "PRICE_ASC" -> Sort.by("price").ascending();
            case "PRICE_DESC" -> Sort.by("price").descending();
            default -> throw new BusinessException(ErrorCode.INVALID_SORT);
        };
    }

    //단건 조회
    public ProductDetailsResponse findById(Long id) {
        Product product = findProductEntity(id);
        return toDetails(product);
    }

    //수정
    @Transactional
    public ProductDetailsResponse update(Long id, UpdateRequest request) {
        Product product = findProductEntity(id);
        product.update(
                request.name(),
                request.price(),
                request.description(),
                request.category()
        );
        return toDetails(product);
    }

    public Product findProductEntity(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    } // 추후 다른 도메인에서 상품 엔티티 사용할때

    public ProductResponse toList(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory(),
                product.getStatus(),
                product.getCreatedAt()
        );
    }

    public ProductDetailsResponse toDetails(Product product) {
        return new ProductDetailsResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getDescription(),
                product.getStatus(),
                product.getCategory(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

}
