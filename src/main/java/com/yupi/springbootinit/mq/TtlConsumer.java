package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class TtlConsumer {

    private final static String QUEUE_NAME = "ttl_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 设置消息队列过期时间 6s
        // Map<String, Object> params = new HashMap<>();
        // params.put("x-message-ttl",6000);
        // channel.queueDeclare(QUEUE_NAME, true, false, false, params);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 定义如何处理消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            // 将消息体转为字符串
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
        };
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
    }
}