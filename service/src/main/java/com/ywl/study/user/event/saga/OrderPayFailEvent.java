package com.ywl.study.user.event.saga;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderPayFailEvent {
    private String orderId;

    private String reason;

}
