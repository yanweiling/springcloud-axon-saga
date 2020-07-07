package com.ywl.study.ticket.event.saga;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 锁票command
 */
@Data
@AllArgsConstructor
public class OrderTicketPreserveFailEvent {

    private String orderId;

}
