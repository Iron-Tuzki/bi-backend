package com.yupi.springbootinit.mapper;

import com.yupi.springbootinit.model.entity.ChartSqlInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
* @author Iron Tuzki
* @description 针对表【chart_sql_info(图表sql信息)】的数据库操作Mapper
* @createDate 2023-07-25 15:55:20
* @Entity com.yupi.springbootinit.model.entity.ChartSqlInfo
*/
public interface ChartSqlInfoMapper extends BaseMapper<ChartSqlInfo> {

    List<Map<String, Object>> queryChartData(String querySql);

    void createChartTable(String sql);

    void insertData(String sql);
}




