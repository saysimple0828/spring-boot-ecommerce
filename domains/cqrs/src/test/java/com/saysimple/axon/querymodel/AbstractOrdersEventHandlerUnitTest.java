package com.saysimple.axon.querymodel;

import com.saysimple.axon.coreapi.events.OrderConfirmedEvent;
import com.saysimple.axon.coreapi.events.OrderCreatedEvent;
import com.saysimple.axon.coreapi.events.OrderShippedEvent;
import com.saysimple.axon.coreapi.events.ProductAddedEvent;
import com.saysimple.axon.coreapi.events.ProductCountDecrementedEvent;
import com.saysimple.axon.coreapi.events.ProductCountIncrementedEvent;
import com.saysimple.axon.coreapi.events.ProductRemovedEvent;
import com.saysimple.axon.coreapi.queries.FindAllOrderedProductsQuery;
import com.saysimple.axon.coreapi.queries.Order;
import com.saysimple.axon.coreapi.queries.OrderStatus;
import com.saysimple.axon.coreapi.queries.OrderUpdatesQuery;
import com.saysimple.axon.coreapi.queries.TotalProductsShippedQuery;

import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.junit.jupiter.api.*;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public abstract class AbstractOrdersEventHandlerUnitTest {

    private static final String ORDER_ID_1 = UUID.randomUUID()
      .toString();
    private static final String ORDER_ID_2 = UUID.randomUUID()
      .toString();
    private static final String PRODUCT_ID_1 = UUID.randomUUID()
      .toString();
    private static final String PRODUCT_ID_2 = UUID.randomUUID()
      .toString();
    private OrdersEventHandler handler;
    private static Order orderOne;
    private static Order orderTwo;
    QueryUpdateEmitter emitter = mock(QueryUpdateEmitter.class);

    @BeforeAll
    static void createOrders() {
        orderOne = new Order(ORDER_ID_1);
        orderOne.getProducts()
          .put(PRODUCT_ID_1, 3);
        orderOne.setOrderShipped();

        orderTwo = new Order(ORDER_ID_2);
        orderTwo.getProducts()
          .put(PRODUCT_ID_1, 1);
        orderTwo.getProducts()
          .put(PRODUCT_ID_2, 1);
        orderTwo.setOrderConfirmed();
    }

    @BeforeEach
    void setUp() {
        handler = getHandler();
    }

    protected abstract OrdersEventHandler getHandler();

    @Test
    @DisplayName("두 개의 주문을 초기화 한 후 모든 주문을 찾는 쿼리를 수행하면 두 개의 주문이 반환된다.")
    void givenTwoOrdersPlacedOfWhichOneNotShipped_whenFindAllOrderedProductsQuery_thenCorrectOrdersAreReturned() {
        resetWithTwoOrders();
        List<Order> result = handler.handle(new FindAllOrderedProductsQuery());

        assertNotNull(result);
        assertEquals(2, result.size());

        Order order_1 = result.stream()
          .filter(o -> o.getOrderId()
            .equals(ORDER_ID_1))
          .findFirst()
          .orElse(null);
        assertEquals(orderOne, order_1);

        Order order_2 = result.stream()
          .filter(o -> o.getOrderId()
            .equals(ORDER_ID_2))
          .findFirst()
          .orElse(null);
        assertEquals(orderTwo, order_2);
    }

    @Test
    @DisplayName("두 개의 주문을 초기화 한 후 모든 주문을 찾는 쿼리를 스트리밍으로 수행하면 두 개의 주문이 반환된다.")
    void givenTwoOrdersPlacedOfWhichOneNotShipped_whenFindAllOrderedProductsQueryStreaming_thenCorrectOrdersAreReturned() {
        resetWithTwoOrders();
        final Consumer<Order> orderVerifier = order -> {
            if (order.getOrderId()
              .equals(orderOne.getOrderId())) {
                assertEquals(orderOne, order);
            } else if (order.getOrderId()
              .equals(orderTwo.getOrderId())) {
                assertEquals(orderTwo, order);
            } else {
                throw new RuntimeException("Would expect either order one or order two");
            }
        };

        StepVerifier.create(Flux.from(handler.handleStreaming(new FindAllOrderedProductsQuery())))
          .assertNext(orderVerifier)
          .assertNext(orderVerifier)
          .expectComplete()
          .verify();
    }

    @Test
    @DisplayName("주문이 없는 경우 총 배송된 제품 쿼리를 수행하면 0이 반환된다.")
    void givenNoOrdersPlaced_whenTotalProductsShippedQuery_thenZeroReturned() {
        assertEquals(0, handler.handle(new TotalProductsShippedQuery(PRODUCT_ID_1)));
    }

    @Test
    @DisplayName("두 개의 주문을 초기화 한 후 각 주문의 총 배송된 제품 쿼리를 수행하면 각 주문의 제품 수가 반환된다.")
    void givenTwoOrdersPlacedOfWhichOneNotShipped_whenTotalProductsShippedQuery_thenOnlyCountProductsFirstOrder() {
        resetWithTwoOrders();

        assertEquals(3, handler.handle(new TotalProductsShippedQuery(PRODUCT_ID_1)));
        assertEquals(0, handler.handle(new TotalProductsShippedQuery(PRODUCT_ID_2)));
    }

    @Test
    @DisplayName("두 개의 주문을 초기화 한 후 두 번째 주문에 대한 주문 배송 이벤트를 수행하면 두 번째 주문의 제품 수가 결과에 반영되어야 한다.")
    void givenTwoOrdersPlacedAndShipped_whenTotalProductsShippedQuery_thenCountBothOrders() {
        resetWithTwoOrders();
        handler.on(new OrderShippedEvent(ORDER_ID_2));

        assertEquals(4, handler.handle(new TotalProductsShippedQuery(PRODUCT_ID_1)));
        assertEquals(1, handler.handle(new TotalProductsShippedQuery(PRODUCT_ID_2)));
    }

    @Test
    @DisplayName("1번 주문에 대한 주문 갱신 쿼리를 수행하면 1번 주문이 반환되고 3개의 제품이 포함되어야 한다.")
    void givenOrderExist_whenOrderUpdatesQuery_thenOrderReturned() {
        resetWithTwoOrders();

        Order result = handler.handle(new OrderUpdatesQuery(ORDER_ID_1));
        assertNotNull(result);
        assertEquals(ORDER_ID_1, result.getOrderId());
        assertEquals(3, result.getProducts()
          .get(PRODUCT_ID_1));
        assertEquals(OrderStatus.SHIPPED, result.getOrderStatus());
    }

    @Test
    @DisplayName("주문 생성 이벤트가 발생하고 상품 추가 이벤트가 발생하면 주문 갱신 쿼리가 한 번 발생해야 한다.")
    void givenOrderExist_whenProductAddedEvent_thenUpdateEmittedOnce() {
        handler.on(new OrderCreatedEvent(ORDER_ID_1));

        handler.on(new ProductAddedEvent(ORDER_ID_1, PRODUCT_ID_1));

        verify(emitter, times(1)).emit(eq(OrderUpdatesQuery.class), any(), any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 이벤트가 발생하고 상품 추가 이벤트가 발생하고 상품 감소 이벤트가 발생하면 주문 갱신 쿼리가 한 번 발생해야 한다.")
    void givenOrderWithProductExist_whenProductCountDecrementedEvent_thenUpdateEmittedOnce() {
        handler.on(new OrderCreatedEvent(ORDER_ID_1));
        handler.on(new ProductAddedEvent(ORDER_ID_1, PRODUCT_ID_1));
        reset(emitter);

        handler.on(new ProductCountDecrementedEvent(ORDER_ID_1, PRODUCT_ID_1));

        verify(emitter, times(1)).emit(eq(OrderUpdatesQuery.class), any(), any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 이벤트가 발생하고 상품 추가 이벤트가 발생하고 상품 제거 이벤트가 발생하면 주문 갱신 쿼리가 한 번 발생해야 한다.")
    void givenOrderWithProductExist_whenProductRemovedEvent_thenUpdateEmittedOnce() {
        handler.on(new OrderCreatedEvent(ORDER_ID_1));
        handler.on(new ProductAddedEvent(ORDER_ID_1, PRODUCT_ID_1));
        reset(emitter);

        handler.on(new ProductRemovedEvent(ORDER_ID_1, PRODUCT_ID_1));

        verify(emitter, times(1)).emit(eq(OrderUpdatesQuery.class), any(), any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 이벤트가 발생하고 상품 추가 이벤트가 발생하고 상품 증가 이벤트가 발생하면 주문 갱신 쿼리가 한 번 발생해야 한다.")
    void givenOrderWithProductExist_whenProductCountIncrementedEvent_thenUpdateEmittedOnce() {
        handler.on(new OrderCreatedEvent(ORDER_ID_1));
        handler.on(new ProductAddedEvent(ORDER_ID_1, PRODUCT_ID_1));
        reset(emitter);

        handler.on(new ProductCountIncrementedEvent(ORDER_ID_1, PRODUCT_ID_1));

        verify(emitter, times(1)).emit(eq(OrderUpdatesQuery.class), any(), any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 이벤트가 발생하고 상품 추가 이벤트가 발생하고 주문 확인 이벤트가 발생하면 주문 갱신 쿼리가 한 번 발생해야 한다.")
    void givenOrderWithProductExist_whenOrderConfirmedEvent_thenUpdateEmittedOnce() {
        handler.on(new OrderCreatedEvent(ORDER_ID_1));
        handler.on(new ProductAddedEvent(ORDER_ID_1, PRODUCT_ID_1));
        reset(emitter);

        handler.on(new OrderConfirmedEvent(ORDER_ID_1));

        verify(emitter, times(1)).emit(eq(OrderUpdatesQuery.class), any(), any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 이벤트가 발생하고 상품 추가 이벤트가 발생하고 주문 배송 이벤트가 발생하면 주문 갱신 쿼리가 한 번 발생해야 한다.")
    void givenOrderWithProductAndConfirmationExist_whenOrderShippedEvent_thenUpdateEmittedOnce() {
        handler.on(new OrderCreatedEvent(ORDER_ID_1));
        handler.on(new ProductAddedEvent(ORDER_ID_1, PRODUCT_ID_1));
        reset(emitter);

        handler.on(new OrderShippedEvent(ORDER_ID_1));

        verify(emitter, times(1)).emit(eq(OrderUpdatesQuery.class), any(), any(Order.class));
    }

    private void resetWithTwoOrders() {
        handler.reset(Arrays.asList(orderOne, orderTwo));
    }
}
