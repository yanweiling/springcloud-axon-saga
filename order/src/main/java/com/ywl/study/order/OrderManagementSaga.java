package com.ywl.study.order;

import com.ywl.study.order.command.OrderFailCommand;
import com.ywl.study.order.command.OrderFinishCommand;
import com.ywl.study.order.event.saga.OrderCreatedEvent;
import com.ywl.study.order.event.saga.OrderFailedEvent;
import com.ywl.study.order.event.saga.OrderFinishedEvent;
import com.ywl.study.ticket.command.OrderTicketMoveCommand;
import com.ywl.study.ticket.command.OrderTicketPreserveCommand;
import com.ywl.study.ticket.command.OrderTicketUnlockCommand;
import com.ywl.study.ticket.event.saga.OrderTicketMovedEvent;
import com.ywl.study.ticket.event.saga.OrderTicketPreserveFailEvent;
import com.ywl.study.ticket.event.saga.OrderTicketPreservedEvent;
import com.ywl.study.user.command.OrderPayCommand;
import com.ywl.study.user.event.saga.OrderPaidEvent;
import com.ywl.study.user.event.saga.OrderPayFailEvent;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.callbacks.LoggingCallback;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage;

/**
 * 对于每一个流程会有一个saga实例
 */
@Saga
public class OrderManagementSaga {
    private static final Logger LOG= LoggerFactory.getLogger(OrderManagementSaga.class);

    /*saga对象会被序列化到数据库中，如果其中属性不想序列化到数据库中，则增加transient*/
    @Autowired
    private transient CommandBus commandBus;

    @Autowired
    private transient EventScheduler eventScheduler;

    /*属性值要想序列化到saga实例中，需要有get set方法*/
    private String orderId;
    private String ticketId;
    private String customerId;
    private Double amount;

    private ScheduleToken scheduleToken;
    /**
     * 当执行OrderCreatedEvent 的时候，下一步要触发执行OrderTicketPreserveCommand
     * @param event
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedEvent event){
        LOG.info("开始创建订单{}",event);
        this.orderId=event.getOrderId();
        this.ticketId=event.getTicketId();
        this.customerId=event.getCustomerId();
        this.amount=event.getAmount();

        //如果30内，saga流程结束，则saga_entry中该实例就会被删除，如果实例删除后，再触发OrderFailedEvent已经没有意义了，也不会影响程序逻辑
        //如果程序中途异常，那么30后saga流程也不会结束，saga_entry中始终存储该saga实例；这个时候触发OrderFailedEvent，就可以对残余saga实例做处理了
//        scheduleToken=eventScheduler.schedule(Instant.now().plusSeconds(30),new OrderFailedEvent(orderId,"Timeout"));
          //由于超时触发orderFailedEvent，无法释放ticket，所以我换成
        scheduleToken=eventScheduler.schedule(Instant.now().plusSeconds(30),new OrderPayFailEvent(orderId,"TimeOut"));


        //生成下一步要执行的command
        OrderTicketPreserveCommand command=new OrderTicketPreserveCommand(event.getTicketId(),event.getOrderId(),event.getCustomerId());
        commandBus.dispatch(asCommandMessage(command), LoggingCallback.INSTANCE);//LoggingCallback.INSTANCE 日志回调方法
    }


    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderTicketPreservedEvent event){
        OrderPayCommand command=new OrderPayCommand(customerId,orderId,amount);
        LOG.info("saga 流程发起OrderPayCommand：{}",command);
        commandBus.dispatch(asCommandMessage(command), LoggingCallback.INSTANCE);//LoggingCallback.INSTANCE 日志回调方法
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderTicketPreserveFailEvent event){
        OrderFailCommand command=new OrderFailCommand(orderId,"lock ticket fail");
        commandBus.dispatch(asCommandMessage(command), LoggingCallback.INSTANCE);//LoggingCallback.INSTANCE 日志回调方法
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderPaidEvent event){

        OrderTicketMoveCommand command=new OrderTicketMoveCommand(ticketId,orderId,customerId);
        commandBus.dispatch(asCommandMessage(command), LoggingCallback.INSTANCE);//LoggingCallback.INSTANCE 日志回调方法
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderTicketMovedEvent event){

        OrderFinishCommand command=new OrderFinishCommand(orderId);
        commandBus.dispatch(asCommandMessage(command), LoggingCallback.INSTANCE);//LoggingCallback.INSTANCE 日志回调方法
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderPayFailEvent event){
        OrderTicketUnlockCommand unlockCommand=new OrderTicketUnlockCommand(ticketId,customerId);
        commandBus.dispatch(asCommandMessage(unlockCommand), LoggingCallback.INSTANCE);//LoggingCallback.INSTANCE 日志回调方法

        OrderFailCommand failCommand=new OrderFailCommand(orderId,"Paid fail:"+event.getReason());//saga 对中文的序列化和返序列化会有问题
        commandBus.dispatch(asCommandMessage(failCommand), LoggingCallback.INSTANCE);//LoggingCallback.INSTANCE 日志回调方法
    }



    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderFailedEvent event){
      //只是标志saga结束，不需要做任何处理
        LOG.info("Order:{} failed.",orderId);
        if(scheduleToken!=null){
            //30s内已经到这里，则后续schedule触发不再执行
            eventScheduler.cancelSchedule(this.scheduleToken);
        }
    }

    //如果saga实例执行正常，但是超过了30s，那么就取消掉OrderFailedEvent事件
    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderFinishedEvent event){
        //只是标志saga结束，不需要做任何处理
        LOG.info("Order:{} finish.",orderId);
        if(scheduleToken!=null){
            //30s内已经到这里，则后续schedule触发不再执行
            eventScheduler.cancelSchedule(this.scheduleToken);
        }
    }

    /**
     * @return the orderId
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * @param orderId to set
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * @return the ticketId
     */
    public String getTicketId() {
        return ticketId;
    }

    /**
     * @param ticketId to set
     */
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    /**
     * @return the customerId
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId to set
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * @return the amount
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * @param amount to set
     */
    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
