package com.ywl.study.ticket.event.saga;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 锁票command
 */
@Data
@AllArgsConstructor
public class OrderTicketPreservedEvent {

    private String ticketId;
    private String orderId;
    private String customerId;
}
