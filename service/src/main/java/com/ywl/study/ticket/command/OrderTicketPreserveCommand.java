package com.ywl.study.ticket.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

/**
 * 锁票command
 */
@Data
@AllArgsConstructor
public class OrderTicketPreserveCommand {
    @TargetAggregateIdentifier
    private String ticketId;
    private String orderId;
    private String customerId;
}
