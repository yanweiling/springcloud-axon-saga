package com.ywl.study.order.event.saga;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderFinishedEvent {
    private String orderId;

}
