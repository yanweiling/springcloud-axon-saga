package com.ywl.study.ticket;

import com.ywl.study.ticket.command.TicketCreateCommand;
import com.ywl.study.ticket.query.TicketEntity;
import com.ywl.study.ticket.query.TicketEntityRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private static final Logger LOG= LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private QueryGateway queryGateway;
    @Autowired
    private TicketEntityRepository repository;

    @PostMapping("")
    public CompletableFuture<Object> create(@RequestParam String name){
        LOG.info("Request to create ticket:{}",name);
        UUID tikcetId= UUID.randomUUID();
        TicketCreateCommand command=new TicketCreateCommand(tikcetId.toString(),name);
        return commandGateway.send(command);
    }

    @GetMapping("")
    public List<TicketEntity> all(){
      return repository.findAll();
    }

}
