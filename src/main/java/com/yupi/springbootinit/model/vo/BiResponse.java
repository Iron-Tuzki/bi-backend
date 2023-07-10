package com.yupi.springbootinit.model.vo;

import lombok.Data;

/**
 * @author lanshu
 * @date 2023-07-07
 */
@Data
public class BiResponse {

    /**
     * 图表生成代码
     */
    private String genChartCode;

    /**
     * 图表分析结论
     */
    private String genResult;

    private Long chartId;
}
