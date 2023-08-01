package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.entity.UserNotification;
import com.yupi.springbootinit.service.UserNotificationService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author lanshu
 * @date 2023-07-31
 */
@Slf4j
@RestController
@RequestMapping("notification")
public class NotificationController {

    @Resource
    private UserNotificationService userNotificationService;

    @Resource
    private UserService userService;

    /**
     * 消息状态更改为已读
     * @param id
     * @param request
     * @return
     */
    @GetMapping("read")
    public BaseResponse<Boolean> changeStatus(@RequestParam("id") long id, HttpServletRequest request) {
        UserNotification userNotification = new UserNotification();
        userNotification.setId(id);
        userNotification.setStatus("read");
        boolean b = userNotificationService.updateById(userNotification);
        if (!b) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "状态更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("getUnread")
    public BaseResponse<Page<UserNotification>> getAllUnread(@RequestParam("current") int current,
                                                             HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        QueryWrapper<UserNotification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("status", "unread");
        Page<UserNotification> page = userNotificationService.page(new Page<>(current, 2), queryWrapper);
        return ResultUtils.success(page);
    }

    @GetMapping("countUnread")
    public BaseResponse<Integer> countUnread(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        QueryWrapper<UserNotification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("status", "unread");
        long count = userNotificationService.count(queryWrapper);
        return ResultUtils.success(Math.toIntExact(count));
    }
}
