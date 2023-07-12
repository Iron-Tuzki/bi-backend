package com.yupi.springbootinit.service;

import com.yupi.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author Iron Tuzki
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2023-07-03 13:57:18
*/
public interface ChartService extends IService<Chart> {


    List<Map<String, Object>> getChartDataById(long id);
}
