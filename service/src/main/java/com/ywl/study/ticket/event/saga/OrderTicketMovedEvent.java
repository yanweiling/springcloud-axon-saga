package com.ywl.study.ticket.event.saga;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderTicketMovedEvent {

    private String ticketId;
    private String orderId;
    private String customerId;
}
