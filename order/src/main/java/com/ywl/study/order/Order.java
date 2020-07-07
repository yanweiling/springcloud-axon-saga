package com.ywl.study.order;

import com.ywl.study.order.command.OrderCreateCommand;
import com.ywl.study.order.command.OrderFailCommand;
import com.ywl.study.order.command.OrderFinishCommand;
import com.ywl.study.order.event.saga.OrderCreatedEvent;
import com.ywl.study.order.event.saga.OrderFailedEvent;
import com.ywl.study.order.event.saga.OrderFinishedEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Aggregate
public class Order {
    private static final Logger LOG= LoggerFactory.getLogger(Order.class);
    @AggregateIdentifier
    private String orderId;

    private String titile;

    private String ticketId;

    private String customerId;

    private Double amount;

    private String reason;

    private ZonedDateTime createDate;

    private String status;

    /**
     * 根据command来注册handler
     * @param command
     */
    @CommandHandler
    public Order(OrderCreateCommand command){
       apply(new OrderCreatedEvent(command.getOrderId(),command.getTitile(),command.getTicketId(),command.getCustomerId(),command.getAmount(),ZonedDateTime.now()));
    }

    @CommandHandler
    public void handle(OrderFinishCommand command){
        apply(new OrderFinishedEvent(command.getOrderId()));
    }
    @CommandHandler
    public void handle(OrderFailCommand command){
        apply(new OrderFailedEvent(command.getOrderId(),command.getReason()));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event){
        this.orderId=event.getOrderId();
        this.amount=event.getAmount();
        this.customerId=event.getCustomerId();
        this.ticketId=event.getTicketId();
        this.titile=event.getTitile();
        this.createDate=event.getCreateDate();
        this.status="NEW";
        LOG.info("Executed event:{}",event);
    }

    @EventSourcingHandler
    public void on(OrderFinishedEvent event){
        this.status="FINISHED";
        LOG.info("Executed event:{}",event);
    }

    @EventSourcingHandler
    public void on(OrderFailedEvent event){
        this.status="FAILED";
        this.reason=event.getReason();
        LOG.info("Executed event:{}",event);
    }

}
