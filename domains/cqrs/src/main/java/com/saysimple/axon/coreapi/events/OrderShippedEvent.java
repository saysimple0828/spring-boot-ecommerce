package com.saysimple.axon.coreapi.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class OrderShippedEvent {

    private final String orderId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final OrderShippedEvent other = (OrderShippedEvent) obj;
        return Objects.equals(this.orderId, other.orderId);
    }

    @Override
    public String toString() {
        return "OrderShippedEvent{" + "orderId='" + orderId + '\'' + '}';
    }
}
