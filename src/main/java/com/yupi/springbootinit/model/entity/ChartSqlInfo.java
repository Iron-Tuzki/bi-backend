package com.yupi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 图表sql信息
 * @TableName chart_sql_info
 */
@TableName(value ="chart_sql_info")
@Data
public class ChartSqlInfo implements Serializable {
    /**
     * 图表id
     */
    @TableId
    private Long chartId;

    /**
     * 列名
     */
    private String headers;

    /**
     * 建表语句
     */
    private String tableSql;

    /**
     * 列名
     */
    private String columnNames;

    /**
     * 数据插入片段
     */
    private String insertSql;

    /**
     * 任务执行状态
     */
    private String status;

    /**
     * 任务执行信息
     */
    private String execMessage;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}