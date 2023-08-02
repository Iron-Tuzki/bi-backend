package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.constant.MQConstant;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.entity.ChartSqlInfo;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.service.ChartSqlInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 死信队列消费者
 *
 * @author lanshu
 * @date 2023-07-21
 */

@Component
@Slf4j
public class DeadLetterConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private ChartSqlInfoService chartSqlInfoService;

    @RabbitListener(queues = {MQConstant.DL_QUEUE_NAME_C}, ackMode = "MANUAL")
    private void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("dead_letter_queue receive:" + message);
            long chartId = Long.parseLong(message);
            Chart chart = new Chart();
            chart.setId(chartId);
            chart.setStatus("fail");
            boolean update = chartService.updateById(chart);
            // todo 通知用户图表任务失败
            if (!update) {
                log.info("更改【图表失败状态】失败。ID：" + chartId);
                channel.basicNack(deliveryTag, false, true);
            }
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = {MQConstant.DL_QUEUE_NAME_SQL}, ackMode = "MANUAL")
    private void receiveMessageSql(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("dead_letter_queue receive:" + message);
            long chartId = Long.parseLong(message);
            ChartSqlInfo chartSqlInfo = new ChartSqlInfo();
            chartSqlInfo.setChartId(chartId);
            chartSqlInfo.setStatus("fail");
            boolean update = chartSqlInfoService.updateById(chartSqlInfo);
            // todo 通知用户sql任务失败
            if (!update) {
                log.info("更改【失败状态】失败。ID：" + chartId);
                channel.basicNack(deliveryTag, false, true);
            }
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
