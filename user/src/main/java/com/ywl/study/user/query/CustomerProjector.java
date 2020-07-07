package com.ywl.study.user.query;

import com.ywl.study.user.event.CustomerChargedEvent;
import com.ywl.study.user.event.CustomerCreatedEvent;
import com.ywl.study.user.event.CustomerDepositedEvent;
import com.ywl.study.user.event.saga.OrderPaidEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 默认情况下，当执行完EventSourceHandler以后，本地的eventProcessor会调用EventHandler去更新物化视图；
 * 如果在分布式环境下，触发eventHandler（除了聚合对象中的handler是本地调用以外）不应该由本地的eventProcessor去调用，而是应该从AMQP中读取消息，
 * 然后再触发evnetHandler
 *
 * 注意：@Aggregate 聚合对象中的所有handler都是本地调用
 *
 * 所以在xxxxProjector中，更改物化视图的时候，应该是从amqp中获取消息，然后触发此类中的eventHandler；
 * 从服务器上获取消息的设置方式就是在该类上加上：@ProcessingGroup("xxxx")
 */
@Service
@ProcessingGroup("UserEventProcessor")
@Slf4j
public class CustomerProjector {
    @Autowired
    private CustomerEntityRepository repository;

    @EventHandler
    public void on(CustomerCreatedEvent event){
        CustomerEntity customer=new CustomerEntity(event.getCustomerId(),event.getUsername(),event.getPassword(),0d);
        repository.save(customer);
    }
    @EventHandler
    public void on(CustomerDepositedEvent event){
        String customerId=event.getCustomerId();
        CustomerEntity accountView=repository.findOne(customerId);
        Double newDeposit=accountView.getDeposit()+event.getAmount();
        accountView.setDeposit(newDeposit);
        repository.save(accountView);
    }

    @EventHandler
    public void on(CustomerChargedEvent event){
        String customerId=event.getCustomerId();
        CustomerEntity accountView=repository.findOne(customerId);
        Double newDeposit=accountView.getDeposit()-event.getAmount();
        accountView.setDeposit(newDeposit);
        repository.save(accountView);
        log.info("CustomerChargedEvent-物化对象金额:{}",newDeposit);
    }

    @EventHandler
    public void on(OrderPaidEvent event){
        String customerId=event.getCustomerId();
        CustomerEntity accountView=repository.findOne(customerId);
        Double newDeposit=accountView.getDeposit()-event.getAmount();
        accountView.setDeposit(newDeposit);
        repository.save(accountView);
        log.info("OrderPaidEvent-物化对象金额:{}",newDeposit);
    }

}
