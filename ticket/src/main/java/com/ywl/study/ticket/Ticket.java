package com.ywl.study.ticket;

import com.ywl.study.ticket.command.OrderTicketMoveCommand;
import com.ywl.study.ticket.command.OrderTicketPreserveCommand;
import com.ywl.study.ticket.command.OrderTicketUnlockCommand;
import com.ywl.study.ticket.command.TicketCreateCommand;
import com.ywl.study.ticket.event.OrderTicketUnlockedEvent;
import com.ywl.study.ticket.event.TicketCreatedEvent;
import com.ywl.study.ticket.event.saga.OrderTicketMovedEvent;
import com.ywl.study.ticket.event.saga.OrderTicketPreserveFailEvent;
import com.ywl.study.ticket.event.saga.OrderTicketPreservedEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Aggregate
@ProcessingGroup("TicketEventProcessor")
public class Ticket {
    private static final Logger LOG= LoggerFactory.getLogger(Ticket.class);
    @AggregateIdentifier
    private String ticketId;
    private String name;

    /*锁票人员*/
    private String lockUser;
    /*锁票后，支付完成，则该票的拥有者*/
    private String owner;

    @CommandHandler
    public Ticket(TicketCreateCommand command){
        LOG.info("");
        apply(new TicketCreatedEvent(command.getTicketId(),command.getName()));
    }

    @CommandHandler
    public void handle(OrderTicketPreserveCommand command){
        if(this.owner!=null){
            LOG.error("Ticket is owned!");
            apply(new OrderTicketPreserveFailEvent(command.getOrderId()));
        }else if(this.lockUser!=null && this.lockUser.equals(command.getCustomerId())){
            LOG.info("duplicated command!");
        }else if(this.lockUser==null){
            apply(new OrderTicketPreservedEvent(command.getTicketId(),command.getOrderId(),command.getCustomerId()));
        }else{
            //已经被他人锁了
            apply(new OrderTicketPreserveFailEvent(command.getOrderId()));
        }
    }

    @CommandHandler
    public void handle(OrderTicketMoveCommand command){
        if(this.lockUser==null){
            LOG.error("Invalid command,tikcet is not locked!");
        }else if(!this.lockUser.equals(command.getCustomerId())){
            LOG.error("Invalid command,ticket is locked by others!");
        }else{
            apply(new OrderTicketMovedEvent(command.getTicketId(),command.getOrderId(),command.getCustomerId()));
        }
    }

    @CommandHandler
    public void handle(OrderTicketUnlockCommand command){
        if(this.lockUser==null){
            LOG.error("Invalid command,tikcet is not locked!");
        }else if(!this.lockUser.equals(command.getCustomerId())){
            LOG.error("Invalid command,ticket is locked by others!");
        }else{
            apply(new OrderTicketUnlockedEvent(command.getTicketId()));
        }
    }

    @EventSourcingHandler
    public void on(TicketCreatedEvent event){
        this.ticketId=event.getTicketId();
        this.name=event.getName();
        LOG.info("Execute event:{}",event);
    }

    @EventSourcingHandler
    public void on(OrderTicketPreservedEvent event){

        this.lockUser=event.getCustomerId();
        LOG.info("Execute event:{}",event);
    }

    @EventSourcingHandler
    public void on(OrderTicketPreserveFailEvent event){
        this.lockUser=null;
        LOG.info("Execute event:{}",event);
    }

    @EventSourcingHandler
    public void on(OrderTicketMovedEvent event){
        this.lockUser=null;
        this.owner=event.getCustomerId();
        LOG.info("Execute event:{}",event);
    }

}
