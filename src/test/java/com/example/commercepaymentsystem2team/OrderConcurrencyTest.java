package com.example.commercepaymentsystem2team;

import com.example.commercepaymentsystem2team.domain.member.entity.Member;
import com.example.commercepaymentsystem2team.domain.member.repository.MemberRepository;
import com.example.commercepaymentsystem2team.domain.order.dto.request.OrderCreateRequest;
import com.example.commercepaymentsystem2team.domain.order.dto.response.OrderCreateResponse;
import com.example.commercepaymentsystem2team.domain.order.entity.Order;
import com.example.commercepaymentsystem2team.domain.order.service.OrderService;
import com.example.commercepaymentsystem2team.domain.product.entity.Product;
import com.example.commercepaymentsystem2team.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class OrderConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 동시에_100명이_주문하면_재고_정합성이_깨지는지_확인한다()
            throws Exception {

        // given
        Product product = productRepository.findByName("스마트폰 X 128GB")
                .orElseThrow();

        product.changeStock(10);

        productRepository.saveAndFlush(product);

        Member member = memberRepository.findById(1L)
                .orElseThrow();

        Long productId = product.getId();
        Long memberId = member.getId();

        int threadCount = 100;

        ExecutorService executorService =
                Executors.newFixedThreadPool(threadCount);

        CountDownLatch startLatch =
                new CountDownLatch(1);

        CountDownLatch endLatch =
                new CountDownLatch(threadCount);

        AtomicInteger successCount =
                new AtomicInteger();

        OrderCreateRequest request =
                new OrderCreateRequest(
                        List.of(
                                new OrderCreateRequest.OrderItemRequest(
                                        productId,
                                        1
                                )
                        )
                );

        // when
        for (int i = 0; i < threadCount; i++) {

            executorService.submit(() -> {

                try {
                    // 모든 스레드가 여기서 대기
                    startLatch.await();

                    // 동시에 주문 시작
                    orderService.createOrder(memberId, request);

                    successCount.incrementAndGet();

                } catch (Exception e) {
                    // 재고 부족 등으로 주문 실패

                } finally {
                    endLatch.countDown();
                }
            });
        }

        // 100개의 스레드 동시에 출발
        startLatch.countDown();

        // 100개 작업이 모두 끝날 때까지 대기
        endLatch.await();

        executorService.shutdown();

        // then
        Product result = productRepository
                .findById(productId)
                .orElseThrow();

        int finalStock = result.getStock();

        System.out.println("성공 주문 수 = " + successCount.get());
        System.out.println("최종 재고 = " + finalStock);

        assertThat(successCount.get())
                .isLessThanOrEqualTo(10);

        assertThat(finalStock)
                .isEqualTo(10 - successCount.get());


    }

    @Test
    void 동시에_같은_주문을_취소하면_재고가_한번만_복구된다()
            throws Exception {

        // given
        Product product = productRepository.findByName("스마트폰 X 128GB")
                .orElseThrow();

        product.changeStock(10);
        productRepository.saveAndFlush(product);

        Member member = memberRepository.findById(1L)
                .orElseThrow();

        Long productId = product.getId();
        Long memberId = member.getId();

        // 주문 생성
        OrderCreateRequest request =
                new OrderCreateRequest(
                        List.of(
                                new OrderCreateRequest.OrderItemRequest(
                                        productId,
                                        1
                                )
                        )
                );

        OrderCreateResponse response =
                orderService.createOrder(memberId, request);

        Long orderId = response.orderId();

        // 주문 후 재고
        Product afterOrder =
                productRepository.findById(productId)
                        .orElseThrow();

        System.out.println("주문 후 재고 = " + afterOrder.getStock());

        int threadCount = 100;

        ExecutorService executorService =
                Executors.newFixedThreadPool(threadCount);

        CountDownLatch startLatch =
                new CountDownLatch(1);

        CountDownLatch endLatch =
                new CountDownLatch(threadCount);

        AtomicInteger successCount =
                new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {

            executorService.submit(() -> {

                try {
                    startLatch.await();

                    Order order = orderService.getCancelableOrder(
                            orderId,
                            memberId
                    );

                    orderService.cancel(
                            order,
                            "동시 취소 테스트"
                    );

                    successCount.incrementAndGet();

                } catch (Exception e) {
                    // 이미 취소된 주문 등으로 실패

                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();

        executorService.shutdown();

        // then
        Product result =
                productRepository.findById(productId)
                        .orElseThrow();

        int finalStock = result.getStock();

        System.out.println("취소 성공 수 = " + successCount.get());
        System.out.println("최종 재고 = " + finalStock);

        assertThat(successCount.get())
                .isEqualTo(1);

        assertThat(finalStock)
                .isEqualTo(10);
    }



}