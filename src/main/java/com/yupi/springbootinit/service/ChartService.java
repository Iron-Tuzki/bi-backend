package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.vo.SimpleChartInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author Iron Tuzki
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2023-07-03 13:57:18
*/
public interface ChartService extends IService<Chart> {



    long genChartAndTable(MultipartFile multipartFile, String name, String goal, String chartType, Long userId);

    List<SimpleChartInfo> getSimpleInfo(Long userId);
}
