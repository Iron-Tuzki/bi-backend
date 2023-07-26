package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.yupi.springbootinit.constant.BiMqConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于创建交换机和队列
 *
 * @author lanshu
 * @date 2023-07-20
 */
public class MqInitMain {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            /* 死信队列交换机和队列
             * 该死信队列用于处理异常，发生异常后标记chart为失败 */
            channel.exchangeDeclare(BiMqConstant.DL_EXCHANGE_NAME, "direct");
            channel.queueDeclare(BiMqConstant.DL_QUEUE_NAME, true, false, false, null);
            channel.queueBind(BiMqConstant.DL_QUEUE_NAME, BiMqConstant.DL_EXCHANGE_NAME, BiMqConstant.DL_ROUTING_KEY);

            channel.queueDeclare(BiMqConstant.DL_QUEUE_SQL_NAME, true, false, false, null);
            channel.queueBind(BiMqConstant.DL_QUEUE_SQL_NAME, BiMqConstant.DL_EXCHANGE_NAME, BiMqConstant.DL_ROUTING_SQL_KEY);

            /* 正常业务交换机和队列
             * params 用于绑定死信交换机 */
            Map<String, Object> params = new HashMap<>();
            params.put("x-dead-letter-exchange", BiMqConstant.DL_EXCHANGE_NAME);
            params.put("x-dead-letter-routing-key", BiMqConstant.DL_ROUTING_KEY);

            channel.exchangeDeclare(BiMqConstant.BI_EXCHANGE_NAME, "direct");
            channel.queueDeclare(BiMqConstant.BI_QUEUE_NAME, true, false, false, params);
            channel.queueBind(BiMqConstant.BI_QUEUE_NAME, BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY);


            Map<String, Object> params1 = new HashMap<>();
            params1.put("x-dead-letter-exchange", BiMqConstant.DL_EXCHANGE_NAME);
            params1.put("x-dead-letter-routing-key", BiMqConstant.DL_ROUTING_SQL_KEY);

            channel.queueDeclare(BiMqConstant.SQL_QUEUE_NAME, true, false, false, params1);
            channel.queueBind(BiMqConstant.SQL_QUEUE_NAME, BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.SQL_ROUTING_KEY);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
