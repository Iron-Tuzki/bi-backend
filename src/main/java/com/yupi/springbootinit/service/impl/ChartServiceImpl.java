package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.bizmq.BiMessageProducer;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.mapper.ChartMapper;
import com.yupi.springbootinit.utils.ExcelUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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

    @Resource
    private ChartMapper chartMapper;

    @Resource
    private BiMessageProducer biMessageProducer;


    @Override
    public List<Map<String, Object>> getChartDataById(long id) {
        String querySql = "select * from chart_" + String.valueOf(id);
        return chartMapper.queryChartData(querySql);
    }

    @Override
    public long genChartAndTable(MultipartFile multipartFile, String name, String goal, String chartType, Long userId) {
        String csv = ExcelUtils.excelToCsv(multipartFile);
        //先存入部分基础数据
        Chart baseInfo = new Chart();
        baseInfo.setName(name);
        baseInfo.setGoal(goal);
        baseInfo.setChartType(chartType);
        baseInfo.setChartData(csv);
        baseInfo.setUserId(userId);
        baseInfo.setStatus("wait");
        boolean b = save(baseInfo);
        if (!b) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表保存失败");
        }

        // 发送消息
        biMessageProducer.sendMessage(String.valueOf(baseInfo.getId()));
        return baseInfo.getId();
    }
}




