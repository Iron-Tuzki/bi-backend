package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.model.entity.ChartSqlInfo;
import com.yupi.springbootinit.service.ChartSqlInfoService;
import com.yupi.springbootinit.mapper.ChartSqlInfoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    public List<Map<String, Object>> getChartDataById(long id) {
        String querySql = "select * from chart_" + String.valueOf(id);
        return chartSqlInfoMapper.queryChartData(querySql);
    }
}




