package com.yupi.springbootinit.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author lanshu
 * @date 2023-07-20
 */
/* 标记为一个组件，spring能扫描到并管理 */
@Component
public class MyMessageProducer {


    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * @param exchangeName
     * @param routingKey
     * @param message
     */
    public void sendMessage(String exchangeName, String routingKey, String message) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    }
}
