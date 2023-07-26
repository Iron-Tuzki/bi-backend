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

    // String SQL_EXCHANGE_NAME = "sql_exchange";

    String SQL_QUEUE_NAME = "sql_queue";

    String SQL_ROUTING_KEY = "sql_routing_key";


    String DL_EXCHANGE_NAME = "dl_exchange";

    String DL_QUEUE_NAME = "dl_queue";

    String DL_ROUTING_KEY = "dead_letter";

    String DL_QUEUE_SQL_NAME = "dl_queue_sql";

    String DL_ROUTING_SQL_KEY = "dead_letter_sql";
}
