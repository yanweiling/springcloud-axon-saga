package com.ywl.study.user.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
public class CustomerChargeCommand {
    /*关联的聚合对象ID*/
    @TargetAggregateIdentifier
    private String customerId;

    /*取款金额*/
    private Double amount;
}
