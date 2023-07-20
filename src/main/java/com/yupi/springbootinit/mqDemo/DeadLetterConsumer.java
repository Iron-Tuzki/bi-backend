package com.yupi.springbootinit.mqDemo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * @author lanshu
 * @date 2023-07-19
 */
public class DeadLetterConsumer {
    // 死信交换机
    private static final String DEAD_EXCHANGE_NAME = "dead_exchange";
    // 正常业务交换机
    private static final String NORMAL_EXCHANGE_NAME = "normal_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 消息接收回调函数，做某些处理
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            // 打印路由键和消息
            System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            // 标记为消费失败，requeue=false 使之成为死信，进入死信队列（若指定）
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
        };
        // autoAck = true 方便测试
        channel.basicConsume("xiaoli", false, deliverCallback, consumerTag -> {
        });
        channel.basicConsume("xiaowang", false, deliverCallback, consumerTag -> {
        });
    }
}
