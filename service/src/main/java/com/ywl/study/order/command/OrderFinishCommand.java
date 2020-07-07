package com.ywl.study.order.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
public class OrderFinishCommand {
    @TargetAggregateIdentifier
    private String orderId;
}
