package com.yupi.springbootinit.service.impl;

import com.yupi.springbootinit.mapper.ChartSqlInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ChartSqlInfoServiceImplTest {

    @Resource
    private ChartSqlInfoMapper mapper;


    @Test
    void createChartTable() {
        // mapper.createChartTable("CREATE TABLE chart_1123\n" +
        //         "(\n" +
        //         "    month         VARCHAR(255) COMMENT '月份',\n" +
        //         "    totalRainfall VARCHAR(255) COMMENT '总降雨'\n" +
        //         ");");

        // mapper.insertData("insert into chart_1684126927913934849 values ('A','90'),('D','33'),('C','55'),('G','30'),('E','83'),('N','38'),('Y','37'),('L','25'),('Q','25'),('K','37');");

        List<Map<String, Object>> maps = mapper.queryChartData("select * from chart_1684126927913934849");
    }
}