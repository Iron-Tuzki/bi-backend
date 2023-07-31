package com.yupi.springbootinit.controller;

import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.service.SysParamsService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lanshu
 * @date 2023-07-31
 */
@RestController
@Slf4j
@RequestMapping("sysParams")
public class SysParamsController {


    @Autowired
    private UserService userService;

    @Resource
    private SysParamsService service;

    @GetMapping("getParams")
    public BaseResponse<Map<String, Boolean>> getParams(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        Map<String, Boolean> sysParams = service.getSysParams(userId);
        return ResultUtils.success(sysParams);
    }

    @GetMapping("switchStatus")
    public BaseResponse<Boolean> switchStatus(@RequestParam(value = "isNotifyChart", required = false) String isNotifyChart,
                                              @RequestParam(value = "isNotifySql", required = false) String isNotifySql,
                                              HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("isNotifyChart", isNotifyChart);
        params.put("isNotifySql", isNotifySql);
        service.updateParams(params);
        return ResultUtils.success(true);
    }
}
