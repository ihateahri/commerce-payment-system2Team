package com.example.commercepaymentsystem2team.domain.product.specification;

import com.example.commercepaymentsystem2team.domain.product.entity.ProductCategory;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductEntity;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductSpecification {
    private ProductSpecification(){

    }
    //카테고리가 null일시
    public static Specification<ProductEntity> hasCategory(ProductCategory category){
        return (root,query,cb)->category==null ? null : cb.equal(root.get("category"), category);
    }

    //최소값
    public static Specification<ProductEntity> priceGte(Long minPrice){
        return (root,query,cb)->minPrice==null ? null : cb.equal(root.get("Price"), minPrice);
    }

    //최고가 있을시
    public static Specification<ProductEntity> priceLte(Long maxPrice){
        return (root,query,cb)->maxPrice==null ? null : cb.equal(root.get("Price"), maxPrice);
    }

    //판매상태 있을시
    public static Specification<ProductEntity> statusIn(List<ProductStatus> statuses){
        return (root,query,cb)->root.get("statuses").in(statuses);
    }
}
