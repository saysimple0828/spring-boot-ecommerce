package com.saysimple.axon.coreapi.commands;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@AllArgsConstructor
public class ShipOrderCommand {
    @TargetAggregateIdentifier String orderId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ShipOrderCommand other = (ShipOrderCommand) obj;
        return Objects.equals(this.orderId, other.orderId);
    }

    @Override
    public String toString() {
        return "ShipOrderCommand{" + "orderId='" + orderId + '\'' + '}';
    }
}
