package com.yupi.springbootinit.mqDemo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class MultiConsumer {

    private static final String TASK_QUEUE_NAME = "multi_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        // for循环创建了两个通道
        for (int i = 0; i < 2; i++) {
            final Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();

            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            // 控制单个通道的处理任务上限
            channel.basicQos(2);

            // 定义如何处理消息
            int finalI = i;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");

                try {
                    System.out.println(finalI + ": [x] Received '" + message + "'");
                    // 模拟机器处理消息能力有限，20s后接收下一条消息
                    Thread.sleep(20000);
                    // doWork(message);
                } catch (InterruptedException e) {
                    // 手动发送’消息接收失败‘，如果requeue=true,则该消息重新进入队列，false则丢弃该消息
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                    throw new RuntimeException(e);
                } finally {
                    System.out.println(" [x] Done");
                    // 手动发送’消息接收成功确认‘，告诉mq消息已经处理，multiple表示批量处理之前一直挤压未发送确认的消息
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                }
            };
            /* autoAck = true 则接收到消息就自动确认’接收成功‘
             * 设置为false时，手动进行确认，最好设置为false*/
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
        }
    }

}