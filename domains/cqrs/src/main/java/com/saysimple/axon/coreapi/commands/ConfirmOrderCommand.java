package com.saysimple.axon.coreapi.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class ConfirmOrderCommand {
    private @TargetAggregateIdentifier String orderId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ConfirmOrderCommand other = (ConfirmOrderCommand) obj;
        return Objects.equals(this.orderId, other.orderId);
    }

    @Override
    public String toString() {
        return "ConfirmOrderCommand{" + "orderId='" + orderId + '\'' + '}';
    }
}
