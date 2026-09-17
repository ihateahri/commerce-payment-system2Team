package com.example.commercepaymentsystem2team.domain.order.repository;

import com.example.commercepaymentsystem2team.domain.order.entity.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.example.commercepaymentsystem2team.domain.order.entity.QOrder.order;

@RequiredArgsConstructor
public class OrderCustomRepositoryImpl implements OrderCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> findAllByMemberId(Long memberId, Pageable pageable) {
        List<Order> content = queryFactory
                .selectFrom(order)
                .where(order.member.id.eq(memberId))
                .orderBy(order.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(order.count())
                .from(order)
                .where(order.member.id.eq(memberId))
                .fetchOne();

        return new PageImpl<>(
                content,
                pageable,
                total == null ? 0 : total
        );
    }

    @Override
    public Optional<Order> findByIdAndMemberId(Long orderId, Long memberId) {
        Order result = queryFactory
                .selectFrom(order)
                .where(
                        order.id.eq(orderId),
                        order.member.id.eq(memberId)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
