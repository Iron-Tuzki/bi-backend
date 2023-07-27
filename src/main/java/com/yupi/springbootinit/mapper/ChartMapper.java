package com.yupi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.vo.SimpleChartInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Iron Tuzki
* @description 针对表【chart(图表信息表)】的数据库操作Mapper
* @createDate 2023-07-03 13:57:18
* @Entity com.yupi.springbootinit.model.entity.Chart
*/
public interface ChartMapper extends BaseMapper<Chart> {


    List<SimpleChartInfo> getSimpleInfo(@Param("userId") Long userId);
}




