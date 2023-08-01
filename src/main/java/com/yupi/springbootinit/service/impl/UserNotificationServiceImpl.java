package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.model.entity.UserNotification;
import com.yupi.springbootinit.service.UserNotificationService;
import com.yupi.springbootinit.mapper.UserNotificationMapper;
import org.springframework.stereotype.Service;

/**
* @author Iron Tuzki
* @description 针对表【user_notification(用户通知表)】的数据库操作Service实现
* @createDate 2023-07-31 13:41:42
*/
@Service
public class UserNotificationServiceImpl extends ServiceImpl<UserNotificationMapper, UserNotification>
    implements UserNotificationService{

}




