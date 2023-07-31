package com.yupi.springbootinit.service;

import java.util.Map;

/**
 * @author lanshu
 * @date 2023-07-31
 */
public interface SysParamsService {

    Map<String, Boolean> getSysParams(long userId);

    /**
     * 更新系统配置
     * @param params
     */
    void updateParams(Map<String, Object> params);
}
