package com.saysimple.order.service;

import com.saysimple.order.dto.OrderDto;
import com.saysimple.order.jpa.OrderEntity;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDetails);
    OrderDto getOrderByOrderId(String orderId);
    Iterable<OrderEntity> getOrdersByUserId(String userId);
}
