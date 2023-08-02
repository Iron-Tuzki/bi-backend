package com.yupi.springbootinit.config;

import com.yupi.springbootinit.constant.MQConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author lanshu
 * @date 2023-08-02
 */
@Configuration
@Slf4j
public class RabbitMqConfig {

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        // 设置其他连接工厂配置，例如用户名和密码
        return connectionFactory;
    }
    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setCorrelationKey(UUID.randomUUID().toString());
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause));
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey, replyCode, replyText, message));
        return rabbitTemplate;
    }

    @Bean
    public DirectExchange dlExchange() {
        return new DirectExchange(MQConstant.DL_EXCHANGE_NAME);
    }

    @Bean
    public Queue queueDLC() {
        return new Queue(MQConstant.DL_QUEUE_NAME_C, true, false, false);
    }

    @Bean
    public Queue queueDLS() {
        return new Queue(MQConstant.DL_QUEUE_NAME_SQL, true,false,false);
    }

    @Bean
    public Binding bindingDLC(Queue queueDLC, DirectExchange dlExchange) {
        return BindingBuilder.bind(queueDLC).to(dlExchange).with(MQConstant.DL_ROUTING_KEY_C);
    }

    @Bean
    public Binding bindingDLS(Queue queueDLS, DirectExchange dlExchange) {
        return BindingBuilder.bind(queueDLS).to(dlExchange).with(MQConstant.DL_ROUTING_KEY_SQL);
    }

/*
    正常业务交换机及队列
*/
    @Bean
    public DirectExchange biExchange() {
        return new DirectExchange(MQConstant.BI_EXCHANGE_NAME);
    }


    @Bean
    public Queue chartQueue() {
        Map<String, Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange", MQConstant.DL_EXCHANGE_NAME);
        params.put("x-dead-letter-routing-key", MQConstant.DL_ROUTING_KEY_C);
        return new Queue(MQConstant.CHART_QUEUE_NAME, true, false, false, params);
    }

    @Bean
    public Queue sqlQueue() {
        Map<String, Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange", MQConstant.DL_EXCHANGE_NAME);
        params.put("x-dead-letter-routing-key", MQConstant.DL_ROUTING_KEY_SQL);
        return new Queue(MQConstant.SQL_QUEUE_NAME, true, false, false, params);
    }


    @Bean
    public Binding bindingC(Queue chartQueue, DirectExchange biExchange) {
        return BindingBuilder.bind(chartQueue).to(biExchange).with(MQConstant.CHART_ROUTING_KEY);
    }

    @Bean
    public Binding bindingS(Queue sqlQueue, DirectExchange biExchange) {
        return BindingBuilder.bind(sqlQueue).to(biExchange).with(MQConstant.SQL_ROUTING_KEY);
    }





}
