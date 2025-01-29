package com.saysimple.axon.coreapi.queries;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class Order {

    private final String orderId;
    private final Map<String, Integer> products;
    private OrderStatus orderStatus;

    public Order(String orderId) {
        this.orderId = orderId;
        this.products = new HashMap<>();
        orderStatus = OrderStatus.CREATED;
    }

    public void addProduct(String productId) {
        products.putIfAbsent(productId, 1);
    }

    public void incrementProductInstance(String productId) {
        products.computeIfPresent(productId, (id, count) -> ++count);
    }

    public void decrementProductInstance(String productId) {
        products.computeIfPresent(productId, (id, count) -> --count);
    }

    public void removeProduct(String productId) {
        products.remove(productId);
    }

    public void setOrderConfirmed() {
        this.orderStatus = OrderStatus.CONFIRMED;
    }

    public void setOrderShipped() {
        this.orderStatus = OrderStatus.SHIPPED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order that = (Order) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(products, that.products) && orderStatus == that.orderStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, products, orderStatus);
    }

    @Override
    public String toString() {
        return "Order{" + "orderId='" + orderId + '\'' + ", products=" + products + ", orderStatus=" + orderStatus + '}';
    }
}
