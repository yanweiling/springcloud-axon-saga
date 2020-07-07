package com.ywl.study.user.event.saga;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderPaidEvent {
    private String customerId;
    private String orderId;
    private Double amount;
}
