package com.yupi.springbootinit.service;

import com.yupi.springbootinit.model.entity.ChartSqlInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author Iron Tuzki
* @description 针对表【chart_sql_info(图表sql信息)】的数据库操作Service
* @createDate 2023-07-25 15:55:20
*/
public interface ChartSqlInfoService extends IService<ChartSqlInfo> {
    List<Map<String, Object>> getChartDataById(long id);
}
