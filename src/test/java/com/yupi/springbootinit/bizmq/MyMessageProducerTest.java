package com.yupi.springbootinit.bizmq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class MyMessageProducerTest {


    @Resource
    private MyMessageProducer messageProducer;
    @Test
    void sendMessage() {

        messageProducer.sendMessage("myExchange","lanshu","你好~~");
    }
}