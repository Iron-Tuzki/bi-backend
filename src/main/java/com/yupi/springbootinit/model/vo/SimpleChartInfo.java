package com.yupi.springbootinit.model.vo;

import lombok.Data;

/**
 * 图表简易信息
 * 用于提交页面里简单展示当前用户的所有图表信息
 * @author lanshu
 * @date 2023-07-27
 */
@Data
public class SimpleChartInfo {

    private long chartId;

    private String name;

    private String chartType;

    private String status;

}
