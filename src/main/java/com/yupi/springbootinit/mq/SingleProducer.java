package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class SingleProducer {

    // 定义一个消息队列，名为 hello
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        //设置主机名 localhost，意味着将会链接到本地rabbitMq服务
        factory.setHost("localhost");
        // factory.setUsername("admin");
        // factory.setPassword("admin");
        try (Connection connection = factory.newConnection();
             // 通过连接创建频道
             Channel channel = connection.createChannel()) {
            /* 在频道上声明一个队列
            durable 消息队列重启后，消息是否丢失（持久化）
             exclusive 只允许创建当前队列的连接操作这个队列（独占性）
             autoDelete
             */
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World11111!";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}