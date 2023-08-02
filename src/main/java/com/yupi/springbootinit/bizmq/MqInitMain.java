package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.yupi.springbootinit.constant.MQConstant;

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
            channel.exchangeDeclare(MQConstant.DL_EXCHANGE_NAME, "direct");
            channel.queueDeclare(MQConstant.DL_QUEUE_NAME_C, true, false, false, null);
            channel.queueBind(MQConstant.DL_QUEUE_NAME_C, MQConstant.DL_EXCHANGE_NAME, MQConstant.DL_ROUTING_KEY_C);

            channel.queueDeclare(MQConstant.DL_QUEUE_NAME_SQL, true, false, false, null);
            channel.queueBind(MQConstant.DL_QUEUE_NAME_SQL, MQConstant.DL_EXCHANGE_NAME, MQConstant.DL_ROUTING_KEY_SQL);

            /* 正常业务交换机和队列
             * params 用于绑定死信交换机 */
            Map<String, Object> params = new HashMap<>();
            params.put("x-dead-letter-exchange", MQConstant.DL_EXCHANGE_NAME);
            params.put("x-dead-letter-routing-key", MQConstant.DL_ROUTING_KEY_C);

            channel.exchangeDeclare(MQConstant.BI_EXCHANGE_NAME, "direct");
            channel.queueDeclare(MQConstant.CHART_QUEUE_NAME, true, false, false, params);
            channel.queueBind(MQConstant.CHART_QUEUE_NAME, MQConstant.BI_EXCHANGE_NAME, MQConstant.CHART_ROUTING_KEY);


            Map<String, Object> params1 = new HashMap<>();
            params1.put("x-dead-letter-exchange", MQConstant.DL_EXCHANGE_NAME);
            params1.put("x-dead-letter-routing-key", MQConstant.DL_ROUTING_KEY_SQL);

            channel.queueDeclare(MQConstant.SQL_QUEUE_NAME, true, false, false, params1);
            channel.queueBind(MQConstant.SQL_QUEUE_NAME, MQConstant.BI_EXCHANGE_NAME, MQConstant.SQL_ROUTING_KEY);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
