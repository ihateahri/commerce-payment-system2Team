package com.example.commercepaymentsystem2team.domain.order.service;

import com.example.commercepaymentsystem2team.common.exception.BusinessException;
import com.example.commercepaymentsystem2team.common.exception.ErrorCode;
import com.example.commercepaymentsystem2team.domain.member.entity.Member;
import com.example.commercepaymentsystem2team.domain.member.repository.MemberRepository;
import com.example.commercepaymentsystem2team.domain.order.dto.request.OrderCreateRequest;
import com.example.commercepaymentsystem2team.domain.order.dto.response.OrderCreateResponse;
import com.example.commercepaymentsystem2team.domain.order.dto.response.OrderDetailResponse;
import com.example.commercepaymentsystem2team.domain.order.dto.response.OrderPageResponse;
import com.example.commercepaymentsystem2team.domain.order.dto.response.OrderPreviewResponse;
import com.example.commercepaymentsystem2team.domain.order.entity.Order;
import com.example.commercepaymentsystem2team.domain.order.entity.OrderItem;
import com.example.commercepaymentsystem2team.domain.order.entity.OrderStatus;
import com.example.commercepaymentsystem2team.domain.order.repository.OrderItemRepository;
import com.example.commercepaymentsystem2team.domain.order.repository.OrderRepository;
import com.example.commercepaymentsystem2team.domain.payment.service.PaymentService;
import com.example.commercepaymentsystem2team.domain.product.entity.Product;
import com.example.commercepaymentsystem2team.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService;


    // =========================
    // 주문 생성
    // =========================
    @Transactional
    public OrderCreateResponse createOrder(
            Long memberId,
            OrderCreateRequest request
    ) {

        // 1. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.MEMBER_NOT_FOUND)
                );

        List<OrderItem> orderItems = new ArrayList<>();

        long totalAmount = 0L;


        // 2. 주문 상품 처리
        for (OrderCreateRequest.OrderItemRequest itemRequest
                : request.items()) {

            // 상품 조회
            Product product = productRepository.findByIdWithLock(itemRequest.productId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            // 3. 재고 검증 + 재고 차감
            product.decreaseStock(
                    itemRequest.quantity()
            );


            // 4. 주문 상품 생성
            OrderItem orderItem = OrderItem.create(
                    product,
                    itemRequest.quantity(),
                    product.getPrice()
            );

            orderItems.add(orderItem);


            // 5. 상품별 주문 금액 계산
            long itemTotalAmount =
                    product.getPrice()
                            * itemRequest.quantity();

            totalAmount += itemTotalAmount;
        }


        // 6. 주문 번호 생성
        String orderNumber =
                UUID.randomUUID().toString();


        // 7. 주문 생성
        Order order = Order.create(
                member,
                orderNumber,
                totalAmount
        );


        // 8. 주문과 주문 상품 연결
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }


        // 9. 주문 저장
        Order savedOrder =
                orderRepository.save(order);


        // 10. 결제 사전 기록 생성
        paymentService.createPayment(
                savedOrder,
                Math.toIntExact(savedOrder.getTotalAmount())
        );


        // 11. 주문 생성 결과 반환
        return new OrderCreateResponse(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus()
        );
    }


    // =========================
    // 주문 상세 조회
    // =========================
    public OrderDetailResponse getOrderDetail(
            Long memberId,
            Long orderId
    ) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.ORDER_NOT_FOUND
                        )
                );


        // 본인의 주문인지 확인
        if (!order.getMember()
                .getId()
                .equals(memberId)) {

            throw new BusinessException(
                    ErrorCode.NO_AUTHORITY
            );
        }


        return OrderDetailResponse.from(order);
    }


    // =========================
    // 주문 목록 조회
    // =========================
    public OrderPageResponse getOrderList(
            Long memberId,
            Pageable pageable
    ) {

        Page<Order> orderPage =
                orderRepository.findByMemberId(
                        memberId,
                        pageable
                );


        return OrderPageResponse.from(orderPage);
    }


    // =========================
    // 주문서 미리보기
    // =========================
    public OrderPreviewResponse previewOrder(
            OrderCreateRequest request
    ) {

        List<OrderPreviewResponse.OrderPreviewItemResponse>
                previewItems = new ArrayList<>();

        long totalAmount = 0L;


        for (OrderCreateRequest.OrderItemRequest itemRequest
                : request.items()) {

            // 상품 조회
            Product product = productRepository
                    .findById(itemRequest.productId())
                    .orElseThrow(() ->
                            new BusinessException(
                                    ErrorCode.PRODUCT_NOT_FOUND
                            )
                    );


            // 상품별 금액 계산
            long totalPrice =
                    product.getPrice()
                            * itemRequest.quantity();


            // 미리보기 상품 정보 생성
            previewItems.add(
                    new OrderPreviewResponse
                            .OrderPreviewItemResponse(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            itemRequest.quantity(),
                            totalPrice
                    )
            );


            totalAmount += totalPrice;
        }


        return new OrderPreviewResponse(
                previewItems,
                totalAmount
        );
    }


    // =========================
    // 취소 가능한 주문 조회
    // 결제 도메인에서 사용
    // =========================
    public Order getCancelableOrder(
            Long orderId,
            Long memberId
    ) {

        // 주문 조회
        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.REFUND_ORDER_NOT_FOUND
                        )
                );


        // 본인의 주문인지 확인
        if (!order.isOwnedBy(memberId)) {
            throw new BusinessException(
                    ErrorCode.ORDER_FORBIDDEN
            );
        }


        // 이미 취소된 주문인지 확인
        if (order.getStatus()
                == OrderStatus.CANCELED) {

            throw new BusinessException(
                    ErrorCode.ALREADY_CANCELED
            );
        }




        return order;
    }


    // =========================
    // 주문 취소 처리
    // 결제 도메인에서 사용
    // =========================
    @Transactional
    public void cancel(Order order, String reason) {

        Order lockedOrder = orderRepository.findByIdWithLock(order.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        lockedOrder.cancel(reason);

        List<OrderItem> orderItems =
                orderItemRepository.findByOrder(lockedOrder);

        orderItems.forEach(item ->
                item.getProduct().increaseStock(item.getQuantity())
        );
    }

    @Transactional
    public void confirmOrder(Order order) {
        order.complete();
    }

    public Order findByIdAndMemberId(Long orderId, Long memberId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.ORDER_NOT_FOUND)
                );

        if (!order.isOwnedBy(memberId)) {
            throw new BusinessException(ErrorCode.NO_AUTHORITY);
        }

        return order;
    }

}