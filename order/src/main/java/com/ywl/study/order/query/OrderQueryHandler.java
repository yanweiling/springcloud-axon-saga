package com.ywl.study.order.query;

import com.ywl.study.order.Order;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.spring.config.AxonConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 为了查看聚合对象中的数据，验证和物化视图中的数据是否一致
 * 正式环境下不建议使用，只限定在开发环境
 */
@Component
public class OrderQueryHandler {
    private static final Logger LOG= LoggerFactory.getLogger(OrderQueryHandler.class);

    @Autowired
    AxonConfiguration axonConfiguration;


    /**
     * 其实本来是要这样设计的
     * @QueryHandler
     * public Order query(String orderId){
     *     ....
     * }
     * 我们预期是给orderId字符串绑定queryHandler的，但是由于orderId类型是String，
     * 这样会遇到，假设我们传了String customerId,或者String ticketId也会绑定QueryHandler，
     * 这样肯定不是我们需要的；
     *
     * 所以这里将String orderId转换成了OrderId,而，这个类中的属性identifier其实就是String orderId，
     * 而其中的hashCode属性只是附加来实现equals方法的
     *
     */

    /*evenSource不建议这么做*/
    @QueryHandler
    public Order query(OrderId orderId){
        LOG.info("Query order info with:{}",orderId.toString());
        final Order[] orders=new Order[1];
        Repository<Order> repository=axonConfiguration.repository(Order.class);//根据聚合对象类型来获取
        repository.load(orderId.toString()).execute(order -> {
            orders[0]=order;
        });
      return orders[0];
    }

}
