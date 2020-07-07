package com.ywl.study.order.event.saga;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * 由event生成固化视图，我们可以把固化视图删除，用event重新生成；
 * 所以event中的字段要足够全，用以生成固化视图
 */
@Data
@AllArgsConstructor
public class OrderCreatedEvent {
    private String orderId;

    private String titile;

    private String ticketId;

    private String customerId;

    private Double amount;

    private ZonedDateTime createDate;
}
