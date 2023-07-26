package com.yupi.springbootinit.bizmq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SqlMessageConsumerTest {

    @Test
    void testExtract() {
        String sql = "CREATE TABLE chart_1684039932604772353 (\n" +
                "        month VARCHAR(255) COMMENT '月份',\n" +
                "totalRainfall VARCHAR(255) COMMENT '总降雨'\n" +
                ");";
        int begin = sql.indexOf('(');
        int end = sql.lastIndexOf(')');
        String substring = sql.substring(begin + 1, end);
        StringBuilder columns = new StringBuilder();
        String[] split = substring.split(",");
        for (String str : split) {
            String[] properties = str.split("\\s+");
            columns.append(properties[1]).append(",");
        }
        System.out.println(columns.toString());
    }
}