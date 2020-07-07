package com.ywl.study.order.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateCommand {

    @TargetAggregateIdentifier
    private String orderId;

    private String titile;

    private String ticketId;

    private String customerId;

    private Double amount;

}
