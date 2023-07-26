package com.yupi.springbootinit.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lanshu
 * @date 2023-07-04
 */
public class ExcelUtils {

    public static String excelToCsv(MultipartFile multipartFile) {

        //读取excel数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (CollUtil.isEmpty(list)) {
            return "";
        }
        // 转为csv格式
        // todo 使用AI生成建表语句
        StringBuilder originalCsv = new StringBuilder();
        LinkedHashMap<Integer, String> headMap = (LinkedHashMap<Integer, String>) list.get(0);
        List<String> headList = headMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        originalCsv.append(StringUtils.join(headList, ",")).append("\n");

        // todo 生成插入语句
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap<Integer, String>) list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            originalCsv.append(StringUtils.join(dataList, ",")).append("\n");
        }
        System.out.println(originalCsv);
        return originalCsv.toString();
    }

    public static Map<String, String> extractData(MultipartFile multipartFile) {

        //读取excel数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (CollUtil.isEmpty(list)) {
            return null;
        }

        Map<String, String> data = new HashMap<>(3);
        StringBuilder insertSql = new StringBuilder();
        // 转为csv格式
        StringBuilder originalCsv = new StringBuilder();
        LinkedHashMap<Integer, String> headMap = (LinkedHashMap<Integer, String>) list.get(0);
        List<String> headList = headMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        originalCsv.append(StringUtils.join(headList, ",")).append("\n");
        String headers = originalCsv.toString();
        data.put("headers", headers.substring(0, headers.length() - 1));

        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap<Integer, String>) list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            String join = StringUtils.join(dataList, ",");
            originalCsv.append(join).append("\n");
            // 解决插入语句格式问题，插入数据统一设为字符串形式，使用''包裹
            if (i == list.size() - 1) {
                insertSql.append("('").append(join.replaceAll(",", "','")).append("');");
            } else {
                insertSql.append("('").append(join.replaceAll(",", "','")).append("'),");
            }
        }

        data.put("originalCsv", originalCsv.toString());
        data.put("insertSql", insertSql.toString());

        return data;
    }

}
