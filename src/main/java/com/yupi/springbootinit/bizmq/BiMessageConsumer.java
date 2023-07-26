package com.yupi.springbootinit.bizmq;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.BiMqConstant;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author lanshu
 * @date 2023-07-20
 */
@Component
@Slf4j
public class BiMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private SqlMessageProducer sqlMessageProducer;

    @Resource
    private AiManager aiManager;

    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            if (StringUtils.isBlank(message)) {
                channel.basicNack(deliveryTag, false, false);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队列消息传输异常");
            }
            log.info("receive message:" + message);
            long chartId = Long.parseLong(message);
            // 更新状态
            Chart updateChart = new Chart();
            updateChart.setId(chartId);
            updateChart.setStatus("running");
            boolean update = chartService.updateById(updateChart);
            if (!update) {
                /* 发生错误，手动标记消息为消费失败, requeue=false 消息转发到死信队列 */
                channel.basicNack(deliveryTag, false, false);
                saveFailMessage(chartId, "状态更改为running失败");
                return;
            }
            // 拼接用户输入，用于提问
            Chart baseInfo = chartService.getById(chartId);
            String chartType = baseInfo.getChartType();
            String goal = baseInfo.getGoal();
            String chartData = baseInfo.getChartData();
            StringBuilder userInput = new StringBuilder();
            userInput.append("你是一个数据分析师和前端开发专家, 我会给你分析需求和原始数据, 请告诉我图表代码及分析结论").append("\n");
            userInput.append("分析需求: 请使用").append(chartType)
                    .append(goal).append("\n");
            userInput.append("原始数据: ").append(chartData).append("\n");

            /* 开始调用Ai接口获取图表信息 */
            log.info("begin invoke AI service 4 bi");
            String result = aiManager.doChat(CommonConstant.CHART_AI_MODEL_ID, userInput.toString(), BiMqConstant.BI_QUEUE_NAME);

            String[] split = result.split("】】】】】");
            if (split.length < 3) {
                channel.basicNack(deliveryTag, false, false);
                saveFailMessage(chartId, "AI生成数据格式出错");
                return;
            }
            String code = split[1].trim();
            String analyzeResult = split[2].trim();
            /* 校验json格式 */
            /* 若流程中发生异常，未手动确认消费成功或失败，则该消息会重新进入原队列。 */
            String chartCode = JSONUtil.toJsonStr(JSONUtil.parseObj(code));
            log.info("end invoke AI service 4 bi");
            /* 结束调用Ai接口获取图表信息 */

            // 更新AI生成的数据
            updateChart.setGenChart(chartCode);
            updateChart.setGenResult(analyzeResult);
            updateChart.setStatus("success");
            boolean up = chartService.updateById(updateChart);
            if (!up) {
                channel.basicNack(deliveryTag, false, false);
                saveFailMessage(chartId, "图表最终数据插入失败");
            }
            // BI图表业务执行成功，确认消费成功
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存错误信息到数据库
     *
     * @param chartId     id
     * @param execMessage 错误信息
     */
    private void saveFailMessage(long chartId, String execMessage) {
        Chart chart = new Chart();
        chart.setId(chartId);
        // chart.setStatus("fail");
        chart.setExecMessage(execMessage);
        boolean update = chartService.updateById(chart);
        if (!update) {
            log.info("保存【图表失败状态】失败。ID：" + chartId);
        }
    }
}
