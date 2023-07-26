package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.constant.CommonConstant;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class AiManagerTest {

    @Resource
    private AiManager aiManager;

    @Test
    void doChat() {

        StringBuilder userInput = new StringBuilder();
        userInput.append("你是一个数据库工程师，擅长写sql语句。\n" +
                "我会告诉你表名和字段的注释，请告诉我mysql的建表语句，不要任何其他内容，字段名为英语且为驼峰格式").append("\n");
        userInput.append("表名：chart_").append("123123").append("\n");
        userInput.append("字段注释：").append("日期，降雨量").append("\n");

        String result = aiManager.doChat(CommonConstant.SQL_AI_MODEL_ID, userInput.toString(), null);
        System.out.println(result);
    }
}