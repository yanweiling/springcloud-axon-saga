package com.ywl.study.ticket.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderTicketUnlockedEvent {

    private String ticketId;

}
