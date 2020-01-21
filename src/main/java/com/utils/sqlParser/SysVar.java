package com.utils.sqlParser;

/**
 * @author wwb
 * @title: SysVar
 * @projectName aliyun-zhibo
 * @description: TODO
 * @date 2019/11/2513:42
 */
import java.util.Map;

public interface SysVar {
    public String getValue(Map<String, String> context) throws Exception;

}