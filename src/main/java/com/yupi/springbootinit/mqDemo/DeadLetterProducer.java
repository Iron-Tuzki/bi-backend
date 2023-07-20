package com.yupi.springbootinit.mqDemo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * @author lanshu
 * @date 2023-07-20
 */
public class DeadLetterProducer {

    // 死信交换机
    private static final String DEAD_EXCHANGE_NAME = "dead_exchange";
    // 正常业务交换机
    private static final String NORMAL_EXCHANGE_NAME = "normal_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 声明死信交换机
        channel.exchangeDeclare(DEAD_EXCHANGE_NAME,"direct");
        String queueName1 = "dead_queue_laoban";
        channel.queueDeclare(queueName1, true, false, false, null);
        channel.queueBind(queueName1, DEAD_EXCHANGE_NAME, "laoban");
        String queueName2 = "dead_queue_waibao";
        channel.queueDeclare(queueName2, true, false, false, null);
        channel.queueBind(queueName2, DEAD_EXCHANGE_NAME, "waibao");

        // 消息接收回调函数，做某些处理
        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            // 打印路由键和消息
            System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            // 标记为消费失败，requeue=true 并丢弃消息
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
        };
        channel.basicConsume(queueName1, false, deliverCallback1, consumerTag -> {
        });
        // 消息接收回调函数，做某些处理
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            // 打印路由键和消息
            System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            // 标记为消费失败，requeue=false 并丢弃消息
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
        };
        channel.basicConsume(queueName2, false, deliverCallback2, consumerTag -> {
        });

        // 声明普通业务交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE_NAME,"direct");
        // 声明普通消息队列xiaoli，该队列的死信通过死信交换机发送到绑定了路由键’laoban‘的死信队列
        String queueName3 = "xiaoli";
        Map<String, Object> param3 = new HashMap<>();
        param3.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        param3.put("x-dead-letter-routing-key", "laoban");
        channel.queueDeclare(queueName3, true, false, false, param3);
        channel.queueBind(queueName3, NORMAL_EXCHANGE_NAME, "xl");
        // 声明普通消息队列，该队列的死信通过死信交换机发送到绑定了路由键’laoban‘的死信队列
        String queueName4 = "xiaowang";
        Map<String, Object> param4 = new HashMap<>();
        param4.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        param4.put("x-dead-letter-routing-key", "waibao");
        channel.queueDeclare(queueName4, true, false, false, param4);
        channel.queueBind(queueName4, NORMAL_EXCHANGE_NAME, "xw");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            String[] split = s.split(" ");
            if (split.length < 1) {
                continue;
            }
            // 生成路由键
            String routingKey = split[0];
            // 生成消息
            String message = split[1];
            // 发布消息到特定交换机（EXCHANGE_NAME）的特定队列（绑定了路由键routingKey）上
            channel.basicPublish(NORMAL_EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");
        }

    }

}
