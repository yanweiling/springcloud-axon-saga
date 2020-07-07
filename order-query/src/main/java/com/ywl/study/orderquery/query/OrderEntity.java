package com.ywl.study.orderquery.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tb_order")
@ToString
public class OrderEntity {
    @Id
    private String orderId;

    private String titile;

    private String ticketId;

    private String customerId;

    private Double amount;

    private String reason;

    private ZonedDateTime createDate;
    private String status;
}
