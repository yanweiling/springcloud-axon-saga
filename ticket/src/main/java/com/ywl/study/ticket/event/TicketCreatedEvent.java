package com.ywl.study.ticket.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketCreatedEvent {
    private String ticketId;
    private String name;
}
