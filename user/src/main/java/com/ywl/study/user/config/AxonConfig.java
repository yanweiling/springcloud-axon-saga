package com.ywl.study.user.config;


import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.serialization.Serializer;
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
    public Exchange exchange(){
      return ExchangeBuilder.topicExchange(exchangeName).durable(true).build();
    }

    @Bean
    public Queue userQueue(){
        return new Queue("user",true);
    }

    @Bean
    public Queue sagaQueue(){
        return new Queue("saga",true);
    }

    /**
     * 当OrderPaidEvent发送到amqp中的时候，可以将该event发送到user队列和saga队列中
     * @return
     */
    @Bean
    public Binding userQueueBinding(){
        return BindingBuilder.bind(userQueue()).to(exchange()).with("com.ywl.study.user.event.#").noargs();
    }
    /**
     * 当OrderPaidEvent发送到amqp中的时候，可以将该event发送到user队列和saga队列中，
     * 这样一个消息就可同时发送到多个队列中
     * @return
     */
    @Bean
    public Binding sagaQueueBinding(){
        return BindingBuilder.bind(sagaQueue()).to(exchange()).with("com.ywl.study.user.event.saga.#").noargs();
    }

    /**
     * user服务从userQueue中读消息，userProjector是根据以下配置方式进行更新物化视图
     */
    @Bean
    public SpringAMQPMessageSource userMessageSource(Serializer serializer){
        return new SpringAMQPMessageSource(serializer){
            @RabbitListener(queues = "user")
            @Override
            public void onMessage(Message message, Channel channel) throws Exception{
                log.info("Message received :{}",message);
                super.onMessage(message,channel);//发送到messageBus上去
            }
        };
    }

    /*设置eventProcessor去使用messageSource*/

    /**
     * UserEventProcessor 会从user队列中读取消息，并触发处理
     * @param ehConfig
     * @param userMessageSource
     */
    @Autowired
    public void configure(EventHandlingConfiguration ehConfig,SpringAMQPMessageSource userMessageSource){
        ehConfig.registerSubscribingEventProcessor("UserEventProcessor",c->userMessageSource);

    }







}
