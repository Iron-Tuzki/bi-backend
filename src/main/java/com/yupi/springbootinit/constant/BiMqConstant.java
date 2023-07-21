package com.yupi.springbootinit.constant;

/**
 * 消息队列常量
 * @author lanshu
 * @date 2023-07-20
 */
public interface BiMqConstant {
    String BI_EXCHANGE_NAME = "bi_exchange";

    String BI_QUEUE_NAME = "bi_queue";

    String BI_ROUTING_KEY = "bi_routing_key";


    String DL_EXCHANGE_NAME = "dl_exchange";

    String DL_QUEUE_NAME = "dl_queue";

    String DL_ROUTING_KEY = "dead_letter";
}
