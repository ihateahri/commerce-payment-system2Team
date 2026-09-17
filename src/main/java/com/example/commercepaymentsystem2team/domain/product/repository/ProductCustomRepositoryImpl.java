package com.example.commercepaymentsystem2team.domain.product.repository;

import com.example.commercepaymentsystem2team.domain.product.entity.Product;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductCategory;
import com.example.commercepaymentsystem2team.domain.product.entity.ProductStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.example.commercepaymentsystem2team.domain.product.entity.QProduct.product;

@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> findAllByCondition(
            ProductCategory category,
            Long minPrice,
            Long maxPrice,
            List<ProductStatus> statuses,
            String sort,
            Pageable pageable
    ) {
        List<Product> content = queryFactory
                .selectFrom(product)
                .where(
                        categoryEq(category),
                        priceGoe(minPrice),
                        priceLoe(maxPrice),
                        statusIn(statuses)
                )
                .orderBy(orderBy(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(product.count())
                .from(product)
                .where(
                        categoryEq(category),
                        priceGoe(minPrice),
                        priceLoe(maxPrice),
                        statusIn(statuses)
                )
                .fetchOne();

        return new PageImpl<>(
                content,
                pageable,
                total == null ? 0 : total
        );
    }

    private BooleanExpression categoryEq(ProductCategory category) {
        return category != null ? product.category.eq(category) : null;
    }

    private BooleanExpression priceGoe(Long minPrice) {
        return minPrice != null ? product.price.goe(minPrice) : null;
    }

    private BooleanExpression priceLoe(Long maxPrice) {
        return maxPrice != null ? product.price.loe(maxPrice) : null;
    }

    private BooleanExpression statusIn(List<ProductStatus> statuses) {
        return statuses != null && !statuses.isEmpty() ? product.status.in(statuses) : null;
    }

    private OrderSpecifier<?> orderBy(String sort) {

        if (sort == null || sort.equals("LATEST")) {
            return product.createdAt.desc();
        }

        return switch (sort) {
            case "PRICE_ASC" -> product.price.asc();
            case "PRICE_DESC" -> product.price.desc();
            default -> product.createdAt.desc();
        };
    }
}
