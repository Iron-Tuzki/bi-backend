package com.yupi.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({"dev","local"})
public class QueueController {

    @Resource
    ThreadPoolExecutor threadPoolExecutor1;

    @GetMapping(value = "add")
    public void addTask(@RequestParam String taskName) {
        CompletableFuture.runAsync(() -> {
            log.info(taskName + "正在执行。执行线程为：" + Thread.currentThread().getName());
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }, threadPoolExecutor1);
    }


    @GetMapping(value = "get")
    public String get() {
        Map<String, Object> map = new HashMap<>();

        map.put("队列长度", threadPoolExecutor1.getQueue().size());

        map.put("任务总数", threadPoolExecutor1.getTaskCount());

        map.put("已完成任务数", threadPoolExecutor1.getCompletedTaskCount());

        map.put("正在工作线程数", threadPoolExecutor1.getActiveCount());

        return JSONUtil.toJsonStr(map);
    }
}
