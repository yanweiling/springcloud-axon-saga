package com.ywl.study.user.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
public class CustomerCreateCommand {
    @TargetAggregateIdentifier
    private String customerId;

    private String username;
    private String password;

}
