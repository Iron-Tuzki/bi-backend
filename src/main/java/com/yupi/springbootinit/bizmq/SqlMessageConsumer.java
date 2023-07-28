package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.BiMqConstant;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.mapper.ChartSqlInfoMapper;
import com.yupi.springbootinit.model.entity.ChartSqlInfo;
import com.yupi.springbootinit.service.ChartSqlInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author lanshu
 * @date 2023-07-25
 */
@Component
@Slf4j
public class SqlMessageConsumer {

    @Resource
    private ChartSqlInfoService chartSqlInfoService;

    @Resource
    private AiManager aiManager;

    @Resource
    private ChartSqlInfoMapper chartSqlInfoMapper;


    @RabbitListener(queues = {BiMqConstant.SQL_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessage(String message,
                               Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            if (StringUtils.isBlank(message)) {
                channel.basicNack(deliveryTag, false, false);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队列消息传输异常");
            }
            log.info("create chart table begin, message=" + message);
            Long chartId = Long.valueOf(message);
            ChartSqlInfo chartSqlInfo = new ChartSqlInfo();
            chartSqlInfo.setChartId(chartId);
            chartSqlInfo.setStatus("running");
            boolean b = chartSqlInfoService.updateById(chartSqlInfo);
            if (!b) {
                /* 发生错误，手动标记消息为消费失败, requeue=false 消息转发到死信队列 */
                channel.basicNack(deliveryTag, false, false);
                saveFailMessage(chartId, "状态更改为running失败");
                return;
            }
            ChartSqlInfo info = chartSqlInfoService.getById(chartId);
            String headers = info.getHeaders();
            String sql4Insert = "insert into chart_" + message + " values " + info.getInsertSql();
            String userInput = "你是一个数据库工程师，擅长写sql语句。\n" +
                    "我会告诉你表名和字段的注释，请告诉我mysql的建表语句，不要任何其他内容，字段名为英语且为驼峰格式\n" +
                    "表名：chart_" + message + "\n" +
                    "字段注释：" + headers + "\n";

            /* 调用AI接口 */
            log.info("begin invoke AI service 4 sql");
            String sql4Table = aiManager.doChat(CommonConstant.SQL_AI_MODEL_ID, userInput, BiMqConstant.SQL_QUEUE_NAME);
            log.info("sql create table :::" + sql4Table);
            log.info("sql insert date :::" + sql4Insert);
            try {
                String columns = extractColumns(sql4Table);
                chartSqlInfo.setColumnNames(columns);
                chartSqlInfo.setTableSql(sql4Table);
                chartSqlInfo.setInsertSql(sql4Insert);
                boolean update = chartSqlInfoService.updateById(chartSqlInfo);
                if (!update) {
                    channel.basicNack(deliveryTag, false, false);
                    saveFailMessage(chartId, "更新chart sql失败");
                    return;
                }
                // sqlExecuteUtils.execute(sql4Table);
                // sqlExecuteUtils.execute(sql4Insert);
                chartSqlInfoMapper.createTable(sql4Table);
                chartSqlInfoMapper.insertData(sql4Insert);
            } catch (RuntimeException e) {
                channel.basicNack(deliveryTag, false, false);
                saveFailMessage(chartId, "sql格式有错误或建表、插入数据失败");
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
            log.info("end invoke AI service 4 sql");

            // 更新状态
            chartSqlInfo.setStatus("success");
            boolean b1 = chartSqlInfoService.updateById(chartSqlInfo);
            if (!b1) {
                channel.basicNack(deliveryTag, false, false);
                saveFailMessage(chartId, "状态更改为success失败");
                return;
            }
            // 所有业务执行成功，确认消费成功
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 从建表语句中提取列名
     * CREATE TABLE chart_1684039932604772353 (
     *   month VARCHAR(255) COMMENT '月份',
     *   totalRainfall VARCHAR(255) COMMENT '总降雨'
     * );
     * @param sql4Table
     * @return
     */
    private String extractColumns(String sql4Table) {
        int begin = sql4Table.indexOf('(');
        int end = sql4Table.lastIndexOf(')');
        String substring = sql4Table.substring(begin + 1, end);
        StringBuilder columns = new StringBuilder();
        String[] split = substring.split(",");
        for (String str : split) {
            String[] properties = str.split("\\s+");
            columns.append(properties[1]).append(",");
        }
        return columns.toString().substring(0, columns.length() - 1);
    }


    /**
     * 保存错误信息到数据库
     *
     * @param chartId     id
     * @param execMessage 错误信息
     */
    private void saveFailMessage(long chartId, String execMessage) {
        ChartSqlInfo chartSqlInfo = new ChartSqlInfo();
        chartSqlInfo.setChartId(chartId);
        // chartSqlInfo.setStatus("fail");
        chartSqlInfo.setExecMessage(execMessage);
        boolean update = chartSqlInfoService.updateById(chartSqlInfo);
        if (!update) {
            log.info("保存【图表失败状态】失败。ID：" + chartId);
        }
    }
}