package com.yupi.springbootinit.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SqlExecuteUtilsTest {

    @Resource
    private SqlExecuteUtils sqlExecuteUtils;
    @Test
    void execute() {
        sqlExecuteUtils.execute("select * from user;");
    }
}