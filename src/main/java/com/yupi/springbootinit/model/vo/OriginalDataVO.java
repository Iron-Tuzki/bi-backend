package com.yupi.springbootinit.model.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lanshu
 * @date 2023-07-31
 */
@Data
public class OriginalDataVO {


    /**
     * 用于控制前端表头columns
     */
    private List<HashMap<String, String>> columns;

    private List<Map<String, Object>> originalData;

}
