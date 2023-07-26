package com.yupi.springbootinit.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.sql.SQLException;

@SpringBootTest
class SqlExecuteUtilsTest {

    @Resource
    private SqlExecuteUtils sqlExecuteUtils;
    @Test
    void execute() {
        try {
            sqlExecuteUtils.execute("CREATE TABLE chart_1684021478451499009 (\n" +
                    "  month VARCHAR(255) COMMENT '月份',\n" +
                    "  totalRainfall VARCHAR(255) COMMENT '总降雨'\n" +
                    ");");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}