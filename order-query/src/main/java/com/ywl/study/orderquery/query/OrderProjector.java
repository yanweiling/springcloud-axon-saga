package com.ywl.study.orderquery.query;

import com.ywl.study.order.event.saga.OrderFailedEvent;
import com.ywl.study.order.event.saga.OrderFinishedEvent;
import com.ywl.study.order.event.saga.OrderCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ProcessingGroup("OrderEventProcessor")
public class OrderProjector {
    private static final Logger LOG= LoggerFactory.getLogger(OrderProjector.class);
    @Autowired
    private OrderEntityRepository repository;
    @EventHandler
    public void on(OrderCreatedEvent event){
        OrderEntity order=new OrderEntity();
        order.setOrderId(event.getOrderId());
        order.setAmount(event.getAmount());
        order.setCustomerId(event.getCustomerId());
        order.setTicketId(event.getTicketId());
        order.setTitile(event.getTitile());
        order.setCreateDate(event.getCreateDate());
        order.setStatus("NEW");
        repository.save(order);
        LOG.info("Executed event:{}",event);
    }

    @EventHandler
    public void on(OrderFinishedEvent event){
        OrderEntity order=repository.findOne(event.getOrderId());
        order.setStatus("FINISHED");
        repository.save(order);
        LOG.info("Executed event:{}",event);
    }

    @EventHandler
    public void on(OrderFailedEvent event){
        OrderEntity order=repository.findOne(event.getOrderId());
        order.setStatus("FAILED");
        order.setReason(event.getReason());
        repository.save(order);
        LOG.info("Executed event:{}",event);
    }
}
