package com.saysimple.orders.service;

import com.saysimple.orders.dto.OrderDto;
import com.saysimple.orders.jpa.OrderEntity;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDetails);
    OrderDto getOrderByOrderId(String orderId);
    Iterable<OrderEntity> getOrdersByUserId(String userId);
}
