package com.yupi.springbootinit.constant;

/**
 * 消息队列常量
 * @author lanshu
 * @date 2023-07-20
 */
public interface MQConstant {
    String BI_EXCHANGE_NAME = "bi_exchange";

    String CHART_QUEUE_NAME = "chart_queue";

    String CHART_ROUTING_KEY = "chart_routing_key";


    String SQL_QUEUE_NAME = "sql_queue";

    String SQL_ROUTING_KEY = "sql_routing_key";


    String DL_EXCHANGE_NAME = "dl_exchange";

    String DL_QUEUE_NAME_C = "dl_queue_c";

    String DL_ROUTING_KEY_C = "dead_letter_c";

    String DL_QUEUE_NAME_SQL = "dl_queue_sql";

    String DL_ROUTING_KEY_SQL = "dead_letter_sql";
}
