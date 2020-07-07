package com.ywl.study.ticket.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
@Data
public class TicketCreateCommand {
    private static final Logger LOG= LoggerFactory.getLogger(TicketCreateCommand.class);
    @TargetAggregateIdentifier
    private String ticketId;
    private String name;

}
