package com.saysimple.axon.coreapi.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class OrderCreatedEvent {
    private final String orderId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderCreatedEvent that = (OrderCreatedEvent) o;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" + "orderId='" + orderId + '\'' + '}';
    }
}
