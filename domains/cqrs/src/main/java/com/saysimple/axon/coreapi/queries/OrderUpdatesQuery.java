package com.saysimple.axon.coreapi.queries;

import java.util.Objects;

public record OrderUpdatesQuery(String orderId) {

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderUpdatesQuery that = (OrderUpdatesQuery) o;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public String toString() {
        return "OrderUpdatesQuery{" + "orderId='" + orderId + '\'' + '}';
    }
}
