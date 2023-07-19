package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * @author lanshu
 * @date 2023-07-19
 */
public class DirectConsumer {

    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        String queueName = "queueA";
        // 声明消息队列
        channel.queueDeclare(queueName, true, false, false, null);
        // 使用路由键“A”绑定队列和交换机
        channel.queueBind(queueName, EXCHANGE_NAME, "A");

        String queueName1 = "queueBC";
        channel.queueDeclare(queueName1, true, false, false, null);
        channel.queueBind(queueName1, EXCHANGE_NAME, "B");
        channel.queueBind(queueName1, EXCHANGE_NAME, "C");
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 消息接收回调函数，做某些处理
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            // 打印路由键和消息
            System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        // autoAck = true 方便测试
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
        channel.basicConsume(queueName1, true, deliverCallback, consumerTag -> {
        });
    }
}
