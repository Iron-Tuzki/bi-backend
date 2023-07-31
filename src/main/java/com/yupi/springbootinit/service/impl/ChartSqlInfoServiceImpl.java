package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.mapper.ChartSqlInfoMapper;
import com.yupi.springbootinit.model.entity.ChartSqlInfo;
import com.yupi.springbootinit.service.ChartSqlInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author Iron Tuzki
* @description 针对表【chart_sql_info(图表sql信息)】的数据库操作Service实现
* @createDate 2023-07-25 15:55:20
*/
@Service
public class ChartSqlInfoServiceImpl extends ServiceImpl<ChartSqlInfoMapper, ChartSqlInfo>
    implements ChartSqlInfoService{

    @Resource
    private ChartSqlInfoMapper chartSqlInfoMapper;

    @Override
    public List<Map<String, Object>> getChartDataById(long chartId) {
        String querySql = "select * from chart_" + chartId;
        return chartSqlInfoMapper.queryChartData(querySql);
    }

    @Override
    public List<HashMap<String, String>> getColumns(long chartId) {
        List<HashMap<String, String>> columns = new ArrayList<>();
        Map<String, String> map = chartSqlInfoMapper.getColumnsAndHeaders(chartId);
        String headerStr = map.get("headers");
        String columnStr = map.get("columnNames");
        String[] headerArr = headerStr.split(",");
        String[] columnArr = columnStr.split(",");
        int length = headerArr.length;
        for (int i = 0; i < length; i++) {
            HashMap<String, String> column = new HashMap<>();
            column.put("title", headerArr[i]);
            column.put("dataIndex", columnArr[i]);
            column.put("key", columnArr[i]);
            columns.add(column);
        }
        return columns;
    }
}




