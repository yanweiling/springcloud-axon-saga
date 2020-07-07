package com.ywl.study.user;

import com.ywl.study.user.command.CustomerChargeCommand;
import com.ywl.study.user.command.CustomerCreateCommand;
import com.ywl.study.user.command.CustomerDepositCommand;
import com.ywl.study.user.query.CustomerEntity;
import com.ywl.study.user.query.CustomerEntityRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private static final Logger LOG= LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private QueryGateway queryGateway;
    @Autowired
    private CustomerEntityRepository repository;

    @PostMapping("")
    public CompletableFuture<Object> create(@RequestParam String name, @RequestParam String password){
        LOG.info("Reqeust to create account for:{}",name);
        UUID accountId= UUID.randomUUID();
        CustomerCreateCommand createCommand=new CustomerCreateCommand(accountId.toString(),name,password);
        return commandGateway.send(createCommand);

    }

    /**
     * 存款
     * @return
     */
    @PutMapping("/{accountId}/deposit/{amount}")
    public CompletableFuture<Object> depositMoney(@PathVariable String accountId, @PathVariable Double amount){
        LOG.info("Reqeust to widhdraw {} to  account :{}",amount,accountId);
        return commandGateway.send(new CustomerDepositCommand(accountId,amount));
    }

    /**
     * 取款
     */
    @PutMapping("/{accountId}/withdraw/{amount}")
    public CompletableFuture<Object> withdrawMoney(@PathVariable String accountId, @PathVariable Double amount){
        LOG.info("Reqeust to widhdraw {} from  account :{}",amount,accountId);
        return commandGateway.send(new CustomerChargeCommand(accountId,amount));
    }

    @GetMapping("/{accountId}")
    public CustomerEntity getCustomerById(@PathVariable String accountId){
        LOG.info("Reqeust customer with id:{}",accountId);
        return repository.findOne(accountId);
    }

    @GetMapping("")
    public List<CustomerEntity> getAllCustomers(){
        LOG.info("Reqeust all customers" );
        return repository.findAll();
    }



}
