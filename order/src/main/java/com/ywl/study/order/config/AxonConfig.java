package com.ywl.study.order.config;


import com.rabbitmq.client.Channel;
import com.ywl.study.order.OrderManagementSaga;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.SagaConfiguration;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.eventhandling.scheduling.java.SimpleEventScheduler;
import org.axonframework.serialization.Serializer;
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


import javax.transaction.Transactional;
import java.util.concurrent.Executors;

/**
 * 只做订单command的处理，不做物化视图的处理,
 * 只需要写到order队列中
 */
@Configuration
@Slf4j
public class AxonConfig {

    /**
     * 我们将消息发送到amqp上，其实是发送到了amqp的brokcer上；
     * 再使用exchange使用绑定的规则，来确定将消息发送到那个queue上
     */
    @Value("${axon.amqp.exchange}")
    private String exchangeName;

    @Bean
    public EventScheduler eventScheduler(EventBus eventBus, TransactionManager transactionManager){
        return new SimpleEventScheduler(Executors.newScheduledThreadPool(1),eventBus,transactionManager);

    }

    @Bean
    public Exchange exchange(){
      return ExchangeBuilder.topicExchange(exchangeName).durable(true).build();
    }

    @Bean
    public Queue orderSagaQueue(){
        return new Queue("saga",true);
    }

    /**
     * 当OrderPaidEvent发送到amqp中的时候，可以将该event发送到user队列和saga队列中，
     * 这样一个消息就可同时发送到多个队列中
     * @return
     */
    @Bean
    public Binding sagaQueueBinding(){
        return BindingBuilder.bind(orderSagaQueue()).to(exchange()).with("com.ywl.study.order.event.saga.#").noargs();
    }

//    @Bean
//    public Queue orderQueue(){
//        return new Queue("order",true);
//    }

//    @Bean
//    public Binding orderQueueBinding(){
//        return BindingBuilder.bind(orderQueue()).to(exchange()).with("com.ywl.study.order.event.#").noargs();
//    }

    /**
     * user服务从userQueue中读消息，userProjector是根据以下配置方式进行更新物化视图
     */
    @Bean
    public SpringAMQPMessageSource sagaMessageSource(Serializer serializer){
        return new SpringAMQPMessageSource(serializer){
            @RabbitListener(queues = "saga")
            @Override
            @Transactional
            public void onMessage(Message message, Channel channel) throws Exception{
                log.info("Message received :{}",message);
                super.onMessage(message,channel);//发送到messageBus上去
            }
        };
    }

    /*设置OrderManagementSagaeventProcessor去使用messageSource*/

    /**
     * UserEventProcessor 会从user队列中读取消息，并触发处理
     * @param transactionManager
     * @param sagaMessageSource
     */
   @Bean
    public SagaConfiguration<OrderManagementSaga> orderManagementSagaSagaConfiguration(SpringAMQPMessageSource sagaMessageSource, PlatformTransactionManager transactionManager){

       return SagaConfiguration.subscribingSagaManager(OrderManagementSaga.class,c->sagaMessageSource)
               .configureTransactionManager(c->new SpringTransactionManager(transactionManager));

    }


}
