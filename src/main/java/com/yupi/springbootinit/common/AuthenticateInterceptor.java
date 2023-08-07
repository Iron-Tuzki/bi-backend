package com.yupi.springbootinit.common;

import com.yupi.springbootinit.model.dto.user.UserTokenDTO;
import com.yupi.springbootinit.service.RedisService;
import com.yupi.springbootinit.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@Component
public class AuthenticateInterceptor implements HandlerInterceptor {

    @Resource
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 在请求被处理之前进行拦截和处理
        // 通过request.getHeader("Authorization")从请求头中获取名为"Authorization"的头部信息，这通常用于传递身份验证凭证。
        // 从authToken中提取出实际的令牌（Token），通过截取字符串的方式，去掉"Bearer"前缀，并去除前后的空格。
        String requestURI = request.getRequestURI();
        // if (requestURI.contains("getLoginUser")) {
        //     return true;
        // }
        String authToken = request.getHeader("Authorization");
        String token = authToken.substring("Bearer".length() + 1).trim();
        UserTokenDTO userTokenDTO = JWTUtils.parseToken(token);
        String userId = String.valueOf(userTokenDTO.getId());
        //1.判断请求是否有效：不存在或者不匹配
        if (redisService.get(userId) == null || !redisService.get(userId).equals(token)) {
            return false;
        }
        //2.判断是否需要续期
        if (redisService.getExpireTime(userId) < 1 * 60 * 30) {
            redisService.set(userId, token);
            log.error("update token info, id is:{}, user info is:{}", userId, token);
        }

        // true 表示继续处理请求，false 表示拦截该请求
        return true;
    }
}