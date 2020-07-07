package com.ywl.study.ticket.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

/**
 * 交票command
 */
@Data
@AllArgsConstructor
public class OrderTicketMoveCommand {
    @TargetAggregateIdentifier
    private String ticketId;
    private String orderId;
    private String customerId;
}
