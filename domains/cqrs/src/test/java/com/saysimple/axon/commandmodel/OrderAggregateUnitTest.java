package com.saysimple.axon.commandmodel;

import com.saysimple.axon.commandmodel.order.OrderAggregate;
import com.saysimple.axon.coreapi.commands.AddProductCommand;
import com.saysimple.axon.coreapi.commands.ConfirmOrderCommand;
import com.saysimple.axon.coreapi.commands.CreateOrderCommand;
import com.saysimple.axon.coreapi.commands.DecrementProductCountCommand;
import com.saysimple.axon.coreapi.commands.IncrementProductCountCommand;
import com.saysimple.axon.coreapi.commands.ShipOrderCommand;
import com.saysimple.axon.coreapi.events.OrderConfirmedEvent;
import com.saysimple.axon.coreapi.events.OrderCreatedEvent;
import com.saysimple.axon.coreapi.events.OrderShippedEvent;
import com.saysimple.axon.coreapi.events.ProductAddedEvent;
import com.saysimple.axon.coreapi.events.ProductCountDecrementedEvent;
import com.saysimple.axon.coreapi.events.ProductCountIncrementedEvent;
import com.saysimple.axon.coreapi.events.ProductRemovedEvent;
import com.saysimple.axon.coreapi.exceptions.DuplicateOrderLineException;
import com.saysimple.axon.coreapi.exceptions.OrderAlreadyConfirmedException;
import com.saysimple.axon.coreapi.exceptions.UnconfirmedOrderException;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.axonframework.test.matchers.Matchers;
import org.junit.jupiter.api.*;

import java.util.UUID;

class OrderAggregateUnitTest {

    private static final String ORDER_ID = UUID.randomUUID()
      .toString();
    private static final String PRODUCT_ID = UUID.randomUUID()
      .toString();

    private FixtureConfiguration<OrderAggregate> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(OrderAggregate.class);
    }

    @Test
    @DisplayName("주문 생성 커맨드를 받으면 주문 생성 이벤트를 발행한다.")
    void giveNoPriorActivity_whenCreateOrderCommand_thenShouldPublishOrderCreatedEvent() {
        fixture.givenNoPriorActivity()
          .when(new CreateOrderCommand(ORDER_ID))
          .expectEvents(new OrderCreatedEvent(ORDER_ID));
    }

    @Test
    @DisplayName("주문 생성 이벤트를 받고 상품 추가 커맨드를 받으면 상품 추가 이벤트를 발행한다.")
    void givenOrderCreatedEvent_whenAddProductCommand_thenShouldPublishProductAddedEvent() {
        fixture.given(new OrderCreatedEvent(ORDER_ID))
          .when(new AddProductCommand(ORDER_ID, PRODUCT_ID))
          .expectEvents(new ProductAddedEvent(ORDER_ID, PRODUCT_ID));
    }

    @Test
    @DisplayName("주문 생성 이벤트와 상품 추가 이벤트를 받고 동일한 상품 추가 커맨드를 받으면 중복 주문 라인 예외를 발생시킨다.")
    void givenOrderCreatedEventAndProductAddedEvent_whenAddProductCommandForSameProductId_thenShouldThrowDuplicateOrderLineException() {
        fixture.given(new OrderCreatedEvent(ORDER_ID), new ProductAddedEvent(ORDER_ID, PRODUCT_ID))
          .when(new AddProductCommand(ORDER_ID, PRODUCT_ID))
          .expectException(DuplicateOrderLineException.class)
          .expectExceptionMessage(Matchers.predicate(message -> ((String) message).contains(PRODUCT_ID)));
    }

    @Test
    @DisplayName("주문 생성 이벤트와 상품 추가 이벤트를 받고 상품 추가 커맨드를 받으면 상품 추가 이벤트를 발행한다.")
    void givenOrderCreatedEventAndProductAddedEvent_whenIncrementProductCountCommand_thenShouldPublishProductCountIncrementedEvent() {
        fixture.given(new OrderCreatedEvent(ORDER_ID), new ProductAddedEvent(ORDER_ID, PRODUCT_ID))
          .when(new IncrementProductCountCommand(ORDER_ID, PRODUCT_ID))
          .expectEvents(new ProductCountIncrementedEvent(ORDER_ID, PRODUCT_ID));
    }

    @Test
    @DisplayName("주문 생성 이벤트와 상품 추가 이벤트와 상품 수량 증가 이벤트를 받고 상품 수량 감소 커맨드를 받으면 상품 수량 감소 이벤트를 발행한다.")
    void givenOrderCreatedEventProductAddedEventAndProductCountIncrementedEvent_whenDecrementProductCountCommand_thenShouldPublishProductCountDecrementedEvent() {
        fixture.given(new OrderCreatedEvent(ORDER_ID), new ProductAddedEvent(ORDER_ID, PRODUCT_ID), new ProductCountIncrementedEvent(ORDER_ID, PRODUCT_ID))
          .when(new DecrementProductCountCommand(ORDER_ID, PRODUCT_ID))
          .expectEvents(new ProductCountDecrementedEvent(ORDER_ID, PRODUCT_ID));
    }

    @Test
    @DisplayName("주문 생성 이벤트와 상품 추가 이벤트를 받고 상품 수량 감소 커맨드를 받으면 상품 제거 이벤트를 발행한다.")
    void givenOrderCreatedEventAndProductAddedEvent_whenDecrementProductCountCommand_thenShouldPublishProductRemovedEvent() {
        fixture.given(new OrderCreatedEvent(ORDER_ID), new ProductAddedEvent(ORDER_ID, PRODUCT_ID))
          .when(new DecrementProductCountCommand(ORDER_ID, PRODUCT_ID))
          .expectEvents(new ProductRemovedEvent(ORDER_ID, PRODUCT_ID));
    }

    @Test
    @DisplayName("주문 생성 이벤트를 받고 주문 확인 커맨드를 받으면 주문 확인 이벤트를 발행한다.")
    void givenOrderCreatedEvent_whenConfirmOrderCommand_thenShouldPublishOrderConfirmedEvent() {
        fixture.given(new OrderCreatedEvent(ORDER_ID))
          .when(new ConfirmOrderCommand(ORDER_ID))
          .expectEvents(new OrderConfirmedEvent(ORDER_ID));
    }

    @Test
    @DisplayName("주문 생성 이벤트와 주문 확인 이벤트를 받고 주문 확인 커맨드를 받으면 이벤트를 발행하지 않는다.")
    void givenOrderCreatedEventAndOrderConfirmedEvent_whenConfirmOrderCommand_thenExpectNoEvents() {
        fixture.given(new OrderCreatedEvent(ORDER_ID), new OrderConfirmedEvent(ORDER_ID))
          .when(new ConfirmOrderCommand(ORDER_ID))
          .expectNoEvents();
    }

    @Test
    @DisplayName("주문 생성 이벤트를 받고 주문 배송 커맨드를 받으면 미확인 주문 예외를 발생시킨다.")
    void givenOrderCreatedEvent_whenShipOrderCommand_thenShouldThrowUnconfirmedOrderException() {
        fixture.given(new OrderCreatedEvent(ORDER_ID))
          .when(new ShipOrderCommand(ORDER_ID))
          .expectException(UnconfirmedOrderException.class);
    }

    @Test
    @DisplayName("주문 생성 이벤트와 주문 확인 이벤트를 받고 주문 배송 커맨드를 받으면 주문 배송 이벤트를 발행한다.")
    void givenOrderCreatedEventAndOrderConfirmedEvent_whenShipOrderCommand_thenShouldPublishOrderShippedEvent() {
        fixture.given(new OrderCreatedEvent(ORDER_ID), new OrderConfirmedEvent(ORDER_ID))
          .when(new ShipOrderCommand(ORDER_ID))
          .expectEvents(new OrderShippedEvent(ORDER_ID));
    }

    @Test
    @DisplayName("주문 생성 이벤트와 주문 확인 이벤트를 받고 상품 추가 커맨드를 받으면 주문 확인 예외를 발생시키고 주문 확인 예외 메시지에 주문 아이디를 포함한다.")
    void givenOrderCreatedEventProductAndOrderConfirmedEvent_whenAddProductCommand_thenShouldThrowOrderAlreadyConfirmedException() {
        fixture.given(new OrderCreatedEvent(ORDER_ID), new OrderConfirmedEvent(ORDER_ID))
          .when(new AddProductCommand(ORDER_ID, PRODUCT_ID))
          .expectException(OrderAlreadyConfirmedException.class)
          .expectExceptionMessage(Matchers.predicate(message -> ((String) message).contains(ORDER_ID)));
    }

    @Test
    @DisplayName("주문 생성 이벤트와 상품 추가 이벤트와 주문 확인 이벤트를 받고 상품 수량 증가 커맨드를 받으면 주문 확인 예외를 발생시키고 주문 확인 예외 메시지에 주문 아이디를 포함한다.")
    void givenOrderCreatedEventProductAddedEventAndOrderConfirmedEvent_whenIncrementProductCountCommand_thenShouldThrowOrderAlreadyConfirmedException() {
        fixture.given(new OrderCreatedEvent(ORDER_ID), new ProductAddedEvent(ORDER_ID, PRODUCT_ID), new OrderConfirmedEvent(ORDER_ID))
          .when(new IncrementProductCountCommand(ORDER_ID, PRODUCT_ID))
          .expectException(OrderAlreadyConfirmedException.class)
          .expectExceptionMessage(Matchers.predicate(message -> ((String) message).contains(ORDER_ID)));
    }

    @Test
    @DisplayName("주문 생성 이벤트와 상품 추가 이벤트와 주문 확인 이벤트를 받고 상품 수량 감소 커맨드를 받으면 이미 확인된 주문 예외를 발생시키고 주문 확인 예외 메시지에 주문 아이디를 포함한다.")
    void givenOrderCreatedEventProductAddedEventAndOrderConfirmedEvent_whenDecrementProductCountCommand_thenShouldThrowOrderAlreadyConfirmedException() {
        fixture.given(new OrderCreatedEvent(ORDER_ID), new ProductAddedEvent(ORDER_ID, PRODUCT_ID), new OrderConfirmedEvent(ORDER_ID))
          .when(new DecrementProductCountCommand(ORDER_ID, PRODUCT_ID))
          .expectException(OrderAlreadyConfirmedException.class)
          .expectExceptionMessage(Matchers.predicate(message -> ((String) message).contains(ORDER_ID)));
    }
}
