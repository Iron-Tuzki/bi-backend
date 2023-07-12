package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.mapper.ChartMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author Iron Tuzki
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2023-07-03 13:57:18
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

    private ChartMapper chartMapper;
    @Override
    public List<Map<String, Object>> getChartDataById(long id) {
        String querySql = "select * from chart_" + String.valueOf(id);
        return chartMapper.queryChartData(querySql);
    }
}




