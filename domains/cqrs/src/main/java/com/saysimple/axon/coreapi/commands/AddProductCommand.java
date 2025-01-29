package com.saysimple.axon.coreapi.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@AllArgsConstructor
public class AddProductCommand {
    @TargetAggregateIdentifier
    private final String orderId;
    private final String productId;

    public String getOrderId() {
        return orderId;
    }

    public String getProductId() {
        return productId;
    }

    @Override
    public String toString() {
        return "AddProductCommand{" + "orderId='" + orderId + '\'' + ", productId='" + productId + '\'' + '}';
    }
}
