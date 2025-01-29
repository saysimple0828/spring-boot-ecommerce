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
import com.saysimple.axon.coreapi.queries.OrderUpdatesQuery;
import com.saysimple.axon.coreapi.queries.TotalProductsShippedQuery;

import org.reactivestreams.Publisher;

import java.util.List;

public interface OrdersEventHandler {

    void on(OrderCreatedEvent event);

    void on(ProductAddedEvent event);

    void on(ProductCountIncrementedEvent event);

    void on(ProductCountDecrementedEvent event);

    void on(ProductRemovedEvent event);

    void on(OrderConfirmedEvent event);

    void on(OrderShippedEvent event);

    List<Order> handle(FindAllOrderedProductsQuery query);

    Publisher<Order> handleStreaming(FindAllOrderedProductsQuery query);

    Integer handle(TotalProductsShippedQuery query);

    Order handle(OrderUpdatesQuery query);

    void reset(List<Order> orderList);
}
