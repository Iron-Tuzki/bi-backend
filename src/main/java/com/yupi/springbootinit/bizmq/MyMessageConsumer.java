package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author lanshu
 * @date 2023-07-20
 */
@Component
@Slf4j
public class MyMessageConsumer {

    /* 在 RabbitMQ 中，消息的 delivery tag 是一个唯一的标识符，用于标识消息在队列中的位置。
    delivery tag 的概念用于确认消息的投递和处理状态。当消费者从队列中获取消息时，会同时返回一个 delivery tag，表示这条消息的标识符。
    消费者处理完消息后，可以使用 delivery tag 来确认消息的处理状态。
    通过确认消息，RabbitMQ 可以将该消息从队列中移除，确保消息只处理一次，避免重复消费。 */
    @RabbitListener(queues = {"myQueue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("receive message:" + message);
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 以上代码等同于
    //     channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
}
