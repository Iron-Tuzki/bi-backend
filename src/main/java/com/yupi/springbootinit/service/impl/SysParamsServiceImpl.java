package com.yupi.springbootinit.service.impl;

import com.yupi.springbootinit.mapper.SysParamsMapper;
import com.yupi.springbootinit.service.SysParamsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lanshu
 * @date 2023-07-31
 */
@Service
public class SysParamsServiceImpl implements SysParamsService {

    @Resource
    private SysParamsMapper sysParamsMapper;
    @Override
    public Map<String, Boolean> getSysParams(long userId) {
        Map<String, String> map = sysParamsMapper.get(userId);
        Map<String, Boolean> resMap = new HashMap<>();
        if ("true".equals(map.get("isNotifyChart"))) {
            resMap.put("isNotifyChart", true);
        } else {
            resMap.put("isNotifyChart", false);
        }
        if ("true".equals(map.get("isNotifySql"))) {
            resMap.put("isNotifySql", true);
        } else {
            resMap.put("isNotifySql", false);
        }
        return resMap;
    }

    @Override
    public void updateParams(Map<String, Object> params) {
        sysParamsMapper.update(params);
    }
}
