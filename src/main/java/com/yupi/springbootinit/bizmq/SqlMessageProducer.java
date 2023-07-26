package com.yupi.springbootinit.bizmq;

import com.yupi.springbootinit.constant.BiMqConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author lanshu
 * @date 2023-07-20
 */
@Component
public class SqlMessageProducer {


    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * @param exchangeName
     * @param routingKey
     * @param message
     */
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.SQL_ROUTING_KEY, message);
    }
}
