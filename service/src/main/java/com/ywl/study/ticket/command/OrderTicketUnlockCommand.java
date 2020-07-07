package com.ywl.study.ticket.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
public class OrderTicketUnlockCommand {
    @TargetAggregateIdentifier
    private String ticketId;

    private String customerId;
}
