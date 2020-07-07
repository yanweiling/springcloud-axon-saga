package com.ywl.study.user.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerDepositedEvent {
    private String customerId;
    /*存款金额*/
    private Double amount;
}
