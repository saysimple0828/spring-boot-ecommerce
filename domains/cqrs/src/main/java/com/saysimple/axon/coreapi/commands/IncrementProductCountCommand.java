package com.saysimple.axon.coreapi.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@AllArgsConstructor
public class IncrementProductCountCommand {
    @TargetAggregateIdentifier
    private String orderId;
    private String productId;

    @Override
    public String toString() {
        return "IncrementProductCountCommand{" + "orderId='" + orderId + '\'' + ", productId='" + productId + '\'' + '}';
    }
}
