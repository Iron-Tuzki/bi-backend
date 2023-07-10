package com.yupi.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiManagerTest {

    @Resource
    private AiManager aiManager;

    @Test
    void doChat() {
        String result = aiManager.doChat(1676401059918065665L, "\n" +
                "你是一个数据分析师和前端开发专家, 我会给你分析目标和原始数据, 请告诉我图表代码和分析结论，图表使用柱状图\n" +
                "分析需求:\n" +
                "分析网站用户增长趋势\n" +
                "原始数据:\n" +
                "日期,用户数\n" +
                "2022-12-1,100\n" +
                "2022-12-2,300\n" +
                "2022-12-3,250\n" +
                "2022-12-4,600");
        System.out.println(result);
    }
}