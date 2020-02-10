package com.mg.community.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @ClassName BaseUtil
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2020/2/10 16:44
 * @Version 1.0
 */
public class BaseUtil {

    /**
     * 将输入的以空格隔开的多个查询名字重新组合，使用“|”分割
     * @param search
     * @return
     */
    public static String seachItemOptimized(String search){
        String searchStr =null;
        if(!StringUtils.isBlank(search)){
            String[] splits = search.split(" ");
            searchStr = Arrays.stream(splits).collect(Collectors.joining("|"));
        }
        return searchStr;
    }
}
