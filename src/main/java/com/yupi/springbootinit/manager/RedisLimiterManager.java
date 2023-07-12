package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.config.RedissonConfig;
import com.yupi.springbootinit.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * @author lanshu
 * @date 2023-07-12
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    public void doRateLimit(String key) {
        // 根据传入参数生成限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        // 设置限流器规则，
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        boolean result = rateLimiter.tryAcquire(1);
        if (!result) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
