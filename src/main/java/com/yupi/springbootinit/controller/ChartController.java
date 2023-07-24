package com.yupi.springbootinit.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.annotation.AuthCheck;
import com.yupi.springbootinit.bizmq.BiMessageProducer;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.DeleteRequest;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.constant.UserConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.manager.RedisLimiterManager;
import com.yupi.springbootinit.model.dto.chart.*;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.BiResponse;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.service.UserService;
import com.yupi.springbootinit.utils.ExcelUtils;
import com.yupi.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 图表接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {


    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;


    @Resource
    private AiManager aiManager;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor1;


    // region 增删改查

    /**
     * 创建
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartId = chart.getId();
        return ResultUtils.success(newChartId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);

        long id = chartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取图表信息
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }

    /**
     * 根据图表ID从对应的表中获取原始数据
     * @param id
     * @return
     */
    @GetMapping("/getData")
    public BaseResponse<List<Map<String, Object>>> getChartDataById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Map<String, Object>> data = chartService.getChartDataById(id);
        if (data == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(data);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 分页获取当前用户创建的图表列表
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/myChartsPage")
    public BaseResponse<Page<Chart>> listMyChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                       HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 编辑（用户）
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }


    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String goal = chartQueryRequest.getGoal();
        String name = chartQueryRequest.getName();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    /**
     * 生成图表（同步）
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/genByAi")
    public BaseResponse<BiResponse> genChartByAI(@RequestPart("file") MultipartFile multipartFile,
                                                 GenChartByAiRequest genChartByAiRequest,
                                                 HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        User loginUser = userService.getLoginUser(request);

        // 表单校验
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100,
                ErrorCode.PARAMS_ERROR, "图表名称过长");
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "分析目标未输入!");
        ThrowUtils.throwIf(StringUtils.isBlank(chartType), ErrorCode.PARAMS_ERROR, "图表类型未输入!");
        //文件校验
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        final long TEN_MB = 10 * 1024 * 1024;
        ThrowUtils.throwIf(size > TEN_MB, ErrorCode.PARAMS_ERROR, "上传文件不能超过10MB");
        final List<String> validSuffix = Arrays.asList("xls", "xlsx");
        if (!validSuffix.contains(suffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "暂不支持" + suffix + "文件类型");
        }
        // 限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit("genChart_" + loginUser.getId());

        StringBuilder userInput = new StringBuilder();
        String csv = ExcelUtils.excelToCsv(multipartFile);
        userInput.append("你是一个数据分析师和前端开发专家, 我会给你分析需求和原始数据, 请告诉我图表代码及分析结论").append("\n");
        userInput.append("分析需求: 请使用").append(chartType).append(goal).append("\n");
        userInput.append("原始数据: ").append(csv).append("\n");

        // 调用Ai接口获取回复
        String result = aiManager.doChat(CommonConstant.TUZKI_AI_MODEL_ID, userInput.toString());

        String[] split = result.split("】】】】】");
        if (split.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成数据格式出错");
        }

        String chartCode = split[1].trim();
        String analyzeResult = split[2].trim();
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartType(chartType);
        chart.setChartData(csv);
        chart.setGenChart(chartCode);
        chart.setGenResult(analyzeResult);
        chart.setUserId(loginUser.getId());
        boolean save = chartService.save(chart);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表保存失败");
        }
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        biResponse.setGenChartCode(chartCode);
        biResponse.setGenResult(analyzeResult);
        return ResultUtils.success(biResponse);
    }


    /**
     * 生成图表（异步）
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/genByAiAsync")
    public BaseResponse<BiResponse> genChartByAIAsync(@RequestPart("file") MultipartFile multipartFile,
                                                 GenChartByAiRequest genChartByAiRequest,
                                                 HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        User loginUser = userService.getLoginUser(request);

        // 表单校验
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100,
                ErrorCode.PARAMS_ERROR, "图表名称过长");
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "分析目标未输入!");
        ThrowUtils.throwIf(StringUtils.isBlank(chartType), ErrorCode.PARAMS_ERROR, "图表类型未输入!");
        //文件校验
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        final long TEN_MB = 10 * 1024 * 1024;
        ThrowUtils.throwIf(size > TEN_MB, ErrorCode.PARAMS_ERROR, "上传文件不能超过10MB");
        final List<String> validSuffix = Arrays.asList("xls", "xlsx");
        if (!validSuffix.contains(suffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "暂不支持" + suffix + "文件类型");
        }
        // 限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit("genChart_"+ loginUser.getId());

        StringBuilder userInput = new StringBuilder();
        String csv = ExcelUtils.excelToCsv(multipartFile);
        userInput.append("你是一个数据分析师和前端开发专家, 我会给你分析需求和原始数据, 请告诉我图表代码及分析结论").append("\n");
        userInput.append("分析需求: 请使用").append(chartType).append(goal).append("\n");
        userInput.append("原始数据: ").append(csv).append("\n");

        //先存入部分数据
        Chart baseInfo = new Chart();
        baseInfo.setName(name);
        baseInfo.setGoal(goal);
        baseInfo.setChartType(chartType);
        baseInfo.setChartData(csv);
        baseInfo.setUserId(loginUser.getId());
        baseInfo.setStatus("wait");
        boolean save = chartService.save(baseInfo);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表保存失败");
        }

        // 异步处理
        CompletableFuture.runAsync(() -> {
            // 更新状态
            Chart chart = new Chart();
            chart.setId(baseInfo.getId());
            chart.setStatus("running");
            boolean update = chartService.updateById(chart);
            if (!update) {
                handleExecutionError(chart.getId(), "状态更改为running失败");
                return;
            }
            // 调用Ai接口获取回复
            String result = aiManager.doChat(CommonConstant.TUZKI_AI_MODEL_ID, userInput.toString());

            String[] split = result.split("】】】】】");
            if (split.length < 3) {
                handleExecutionError(chart.getId(), "生成数据格式出错");
                return;
            }
            // 更新AI生成的数据
            String chartCode = split[1].trim();
            String analyzeResult = split[2].trim();
            chart.setGenChart(chartCode);
            chart.setGenResult(analyzeResult);
            chart.setStatus("success");
            boolean up = chartService.updateById(chart);
            if (!up) {
                handleExecutionError(chart.getId(), "图表最终数据插入失败");
            }

        }, threadPoolExecutor1);

        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(baseInfo.getId());
        return ResultUtils.success(biResponse);
    }

    private void handleExecutionError(long chartId, String execMessage) {
        Chart chart = new Chart();
        chart.setId(chartId);
        chart.setStatus("fail");
        chart.setExecMessage(execMessage);
        boolean update = chartService.updateById(chart);
        if (!update) {
            log.info("保存【图表失败状态】失败。ID：" + chartId);
        }
    }

    /**
     * 生成图表（消息队列）
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/genByAiMq")
    public BaseResponse<BiResponse> genChartByAIMq(@RequestPart("file") MultipartFile multipartFile,
                                                      GenChartByAiRequest genChartByAiRequest,
                                                      HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        Long userId = userService.getLoginUser(request).getId();

        // 表单校验
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100,
                ErrorCode.PARAMS_ERROR, "图表名称过长");
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "分析目标未输入!");
        ThrowUtils.throwIf(StringUtils.isBlank(chartType), ErrorCode.PARAMS_ERROR, "图表类型未输入!");
        //文件校验
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        final long TEN_MB = 10 * 1024 * 1024;
        ThrowUtils.throwIf(size > TEN_MB, ErrorCode.PARAMS_ERROR, "上传文件不能超过10MB");
        final List<String> validSuffix = Arrays.asList("xls", "xlsx");
        if (!validSuffix.contains(suffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "暂不支持" + suffix + "文件类型");
        }
        // 限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit("genChart_"+ userId);

        long chartId = chartService.genChartAndTable(multipartFile, name, goal, chartType, userId);

        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chartId);
        return ResultUtils.success(biResponse);
    }

}



