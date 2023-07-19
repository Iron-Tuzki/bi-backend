package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;
/**
 * @author lanshu
 * @date 2023-07-19
 */
public class DirectProducer {

    // 日志系统的交换机
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

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
                channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");
            }
        }
    }
}
