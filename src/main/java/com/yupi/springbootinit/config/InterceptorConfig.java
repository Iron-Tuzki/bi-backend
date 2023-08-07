package com.yupi.springbootinit.config;

import com.yupi.springbootinit.common.AuthenticateInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 全局跨域配置
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private AuthenticateInterceptor authenticateInterceptor;

    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    //     // 覆盖所有请求
    //     registry.addMapping("/**")
    //             // 允许发送 Cookie
    //             .allowCredentials(true)
    //             // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
    //             .allowedOriginPatterns("*")
    //             .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    //             .allowedHeaders("*")
    //             .exposedHeaders("*");
    // }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticateInterceptor)
                .excludePathPatterns("/**/logout/**")
                .excludePathPatterns("/**/login/**")
                .addPathPatterns("/**");
    }
}
