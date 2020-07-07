package com.ywl.study.ticket.query;

import com.ywl.study.ticket.event.saga.OrderTicketMovedEvent;
import com.ywl.study.ticket.event.saga.OrderTicketPreservedEvent;
import com.ywl.study.ticket.event.OrderTicketUnlockedEvent;
import com.ywl.study.ticket.event.TicketCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ProcessingGroup(value = "TicketEventProcessor")
public class TicketProjector {
    private static final Logger LOG= LoggerFactory.getLogger(TicketProjector.class);
    @Autowired
    private TicketEntityRepository repository;

    @EventHandler
    public void on(TicketCreatedEvent event){
        TicketEntity ticket=new TicketEntity();
        ticket.setTicketId(event.getTicketId());
        ticket.setName(event.getName());
        ticket.setLockUser(null);
        ticket.setOwner(null);
        repository.save(ticket);
        LOG.info("Execute event:{} by TicketProjector",event);
    }

    @EventHandler
    public void on(OrderTicketPreservedEvent event){
        TicketEntity ticket=repository.findOne(event.getTicketId());
        ticket.setLockUser(event.getCustomerId());
        repository.save(ticket);
        LOG.info("Execute event:{} by TicketProjector",event);
    }

    @EventHandler
    public void on(OrderTicketMovedEvent event){
        TicketEntity ticket=repository.findOne(event.getTicketId());
        ticket.setLockUser(null);
        ticket.setOwner(event.getCustomerId());
        repository.save(ticket);
        LOG.info("Execute event:{} by TicketProjector",event);
    }

    @EventHandler
    public void on(OrderTicketUnlockedEvent event){
        TicketEntity ticket=repository.findOne(event.getTicketId());
        ticket.setLockUser(null);
        repository.save(ticket);
        LOG.info("Execute event:{} by TicketProjector",event);
    }
}
