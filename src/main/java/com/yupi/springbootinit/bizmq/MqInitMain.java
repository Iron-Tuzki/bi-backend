package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 用于创建交换机和队列
 * @author lanshu
 * @date 2023-07-20
 */
public class MqInitMain {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare("myExchange", "direct");
            channel.queueDeclare("myQueue", true, false, false, null);
            channel.queueBind("myQueue", "myExchange", "lanshu");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
