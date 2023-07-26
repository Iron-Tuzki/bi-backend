package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.bizmq.BiMessageProducer;
import com.yupi.springbootinit.bizmq.SqlMessageProducer;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.mapper.ChartMapper;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.entity.ChartSqlInfo;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.service.ChartSqlInfoService;
import com.yupi.springbootinit.utils.ExcelUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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
    private ChartSqlInfoService chartSqlInfoService;

    @Resource
    private BiMessageProducer biMessageProducer;

    @Resource
    private SqlMessageProducer sqlMessageProducer;




    @Override
    public long genChartAndTable(MultipartFile multipartFile, String name, String goal, String chartType, Long userId) {

        Map<String, String> originalDate = ExcelUtils.extractData(multipartFile);
        //先存入部分基础数据
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartType(chartType);
        chart.setChartData(originalDate.get("originalCsv"));
        chart.setUserId(userId);
        chart.setStatus("wait");
        boolean b = save(chart);
        if (!b) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表信息保存失败");
        }
        ChartSqlInfo chartSqlInfo = new ChartSqlInfo();
        chartSqlInfo.setChartId(chart.getId());
        chartSqlInfo.setHeaders(originalDate.get("headers"));
        chartSqlInfo.setInsertSql(originalDate.get("insertSql"));
        chartSqlInfo.setStatus("wait");
        boolean save = chartSqlInfoService.save(chartSqlInfo);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表sql信息保存失败");
        }

        // 发送消息，生成建表语句，并建表，插入数据
        sqlMessageProducer.sendMessage(String.valueOf(chart.getId()));
        // 发送消息，生成图表代码
        biMessageProducer.sendMessage(String.valueOf(chart.getId()));

        return chart.getId();
    }
}




