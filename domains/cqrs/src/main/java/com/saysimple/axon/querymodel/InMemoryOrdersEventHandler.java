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

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@ProcessingGroup("orders")
@Profile("!mongo")
public class InMemoryOrdersEventHandler implements OrdersEventHandler {

    private final Map<String, Order> orders = new HashMap<>();
    private final QueryUpdateEmitter emitter;

    public InMemoryOrdersEventHandler(QueryUpdateEmitter emitter) {
        this.emitter = emitter;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        String orderId = event.getOrderId();
        orders.put(orderId, new Order(orderId));
    }

    @EventHandler
    public void on(ProductAddedEvent event) {
        orders.computeIfPresent(event.getOrderId(), (orderId, order) -> {
            order.addProduct(event.getProductId());
            emitUpdate(order);
            return order;
        });
    }

    @EventHandler
    public void on(ProductCountIncrementedEvent event) {
        orders.computeIfPresent(event.getOrderId(), (orderId, order) -> {
            order.incrementProductInstance(event.getProductId());
            emitUpdate(order);
            return order;
        });
    }

    @EventHandler
    public void on(ProductCountDecrementedEvent event) {
        orders.computeIfPresent(event.getOrderId(), (orderId, order) -> {
            order.decrementProductInstance(event.getProductId());
            emitUpdate(order);
            return order;
        });
    }

    @EventHandler
    public void on(ProductRemovedEvent event) {
        orders.computeIfPresent(event.getOrderId(), (orderId, order) -> {
            order.removeProduct(event.getProductId());
            emitUpdate(order);
            return order;
        });
    }

    @EventHandler
    public void on(OrderConfirmedEvent event) {
        orders.computeIfPresent(event.getOrderId(), (orderId, order) -> {
            order.setOrderConfirmed();
            emitUpdate(order);
            return order;
        });
    }

    @EventHandler
    public void on(OrderShippedEvent event) {
        orders.computeIfPresent(event.getOrderId(), (orderId, order) -> {
            order.setOrderShipped();
            emitUpdate(order);
            return order;
        });
    }

    @QueryHandler
    public List<Order> handle(FindAllOrderedProductsQuery query) {
        return new ArrayList<>(orders.values());
    }

    @QueryHandler
    public Publisher<Order> handleStreaming(FindAllOrderedProductsQuery query) {
        return Mono.fromCallable(orders::values)
          .flatMapMany(Flux::fromIterable);
    }

    @QueryHandler
    public Integer handle(TotalProductsShippedQuery query) {
        return orders.values()
          .stream()
          .filter(o -> o.getOrderStatus() == OrderStatus.SHIPPED)
          .map(o -> Optional.ofNullable(o.getProducts()
              .get(query.productId()))
            .orElse(0))
          .reduce(0, Integer::sum);
    }

    @QueryHandler
    public Order handle(OrderUpdatesQuery query) {
        return orders.get(query.orderId());
    }

    private void emitUpdate(Order order) {
        emitter.emit(OrderUpdatesQuery.class, q -> order.getOrderId()
          .equals(q.orderId()), order);
    }

    @Override
    public void reset(List<Order> orderList) {
        orders.clear();
        orderList.forEach(o -> orders.put(o.getOrderId(), o));
    }
}
