package com.yupi.springbootinit.service.impl;

import com.yupi.springbootinit.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author lanshu
 * @date 2023-08-03
 */
@Service
@Slf4j
public final class RedisServiceImpl implements RedisService {

    /**
     * 过期时长
     */
    private final Long DURATION = 1 * 24 * 60 * 60 * 1000L;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private ValueOperations<String, String> valueOperations;


    @PostConstruct
    public void init() {
        valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public void set(String key, String value) {
        valueOperations.set(key, value, DURATION, TimeUnit.MILLISECONDS);
        log.info("key={}, value is: {} into redis cache", key, value);
    }

    @Override
    public String get(String key) {
        String redisValue = valueOperations.get(key);
        log.info("get from redis, value is: {}", redisValue);
        return redisValue;
    }

    @Override
    public boolean delete(String key) {
        boolean result = Boolean.TRUE.equals(redisTemplate.delete(key));
        log.info("delete from redis, key is: {}", key);
        return result;
    }

    @Override
    public Long getExpireTime(String key) {
        return valueOperations.getOperations().getExpire(key);
    }

    @Override
    public void set(String key, Object value, Long expireTime) {
    }

    @Override
    public void remove(String... keys) {

    }

    @Override
    public void removePattern(String pattern) {

    }

    @Override
    public void remove(String key) {

    }

    @Override
    public boolean exists(String key) {
        return false;
    }


    @Override
    public void hmSet(String key, Object hashKey, Object value) {

    }

    @Override
    public Object hmGet(String key, Object hashKey) {
        return null;
    }

    @Override
    public void lPush(String k, Object v) {

    }

    @Override
    public List<Object> lRange(String k, long l, long l1) {
        return null;
    }

    @Override
    public void add(String key, Object value) {

    }

    @Override
    public Set<Object> setMembers(String key) {
        return null;
    }

    @Override
    public void zAdd(String key, Object value, double scoure) {

    }

    @Override
    public Set<Object> rangeByScore(String key, double scoure, double scoure1) {
        return null;
    }
}
