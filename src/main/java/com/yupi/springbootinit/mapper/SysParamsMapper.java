package com.yupi.springbootinit.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author lanshu
 * @date 2023-07-31
 */
@Repository
public interface SysParamsMapper {
    Map<String, String> get(long userId);

    void update(Map<String, Object> params);
}
