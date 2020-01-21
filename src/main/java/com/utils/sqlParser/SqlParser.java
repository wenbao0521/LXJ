package com.utils.sqlParser;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SqlParser {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 解析sql
     *
     * @param sql
     * @param ctxMap
     * @return
     * @throws Exception
     */
    public static String parse(String sql, Map<String, String> ctxMap) throws Exception {
        //1、校验
        if (sql == null || "".equals(sql)) {
            return sql;
        }

        //2、预处理
        sql = preprocess(sql, ctxMap);

        //3、替换变量
        if (sql.indexOf("~") == -1) {
            return sql;
        }
        sql = replace(sql, ctxMap, "");

        //4、返回sql
        return sql;
    }


    /**
     * 预处理
     */
    private static String preprocess(String sql, Map<String, String> ctxMap) throws Exception {
        //获得变量的起始标记位置
        int start = sql.indexOf("{");
        if (start == -1) {
            return sql;
        }
        int end = sql.indexOf("}");
        //获得变量
        String variable = sql.substring(start + 1, end);
        //获得变量的值
        String value = null;
        if (variable.startsWith("s_")) {//系统变量
            SysVar sysVar = (SysVar) SpringContextUtil.getBean(variable);
            value = sysVar.getValue(null);
        } else {//用户变量
            value = ctxMap.get(variable);
        }

        //获得变量的性质
        int start2 = sql.indexOf("[");
        int end2 = sql.indexOf("]");

        if (value == null) {
            if (start2 != -1 && start2 < start) {
                sql = sql.substring(0, start2) + sql.substring(end2 + 1);
                int start3 = sql.indexOf("$");
                int end3 = sql.indexOf("#");
                if (start3 != -1 && end3 != -1) {
                    sql = sql.substring(0, start3) + sql.substring(end3 + 1);
                }
            } else {
                throw new Exception("In parameter map(" + ctxMap.toString() + "),there is not a value for variable '" + variable + "'");
            }
        } else {
            sql = sql.replaceFirst("\\{", "~");
            sql = sql.replaceFirst("\\}", "@");
            if (start2 != -1 && start2 < start) {
                sql = sql.replaceFirst("\\[", "");
                sql = sql.replaceFirst("\\]", "");
                sql = sql.replaceFirst("\\$", "");
                sql = sql.replaceFirst("\\#", "");
            }
        }
        return preprocess(sql, ctxMap);
    }

    /**
     * 替换变量
     *
     * @return
     * @throws Exception
     */
    private static String replace(String sql, Map<String, String> ctxMap, String sql2) throws Exception {
        //获得变量的起始标记位置
        int start = sql.indexOf("~");
        if (start == -1) {
            return sql2 + sql;
        }
        int end = sql.indexOf("@");
        //获得变量
        String variable = sql.substring(start + 1, end);
        //获得变量的值
        String value = null;
        if (variable.startsWith("s_")) {//系统变量
            SysVar sysVar = (SysVar) SpringContextUtil.getBean(variable);
            value = sysVar.getValue(null);
        } else {//用户变量
            value = ctxMap.get(variable);
        }

        sql2 += sql.substring(0, start);
        sql2 += value;

        sql = sql.substring(end + 1);

        return replace(sql, ctxMap, sql2);
    }

    public static String addPagingInfo(String sql) {
        return "select * from (select t.*,rownum rn2 from (" + sql + ") t) t where rn2 between {start} and {start} + {limit} - 1";
    }


    public static String addNewPagingInfo(String sql) {
        return "select * from (select t.*,rownum rn2 from (" + sql + ") t) t where rn2 between {offset} + 1 and {offset} + {limit} ";
    }



    public static Map<String, String> escape4select(Map<String, String> paramsMap) {
        Map<String, String> ctxMap = new HashMap();
        Iterator<String> it = paramsMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = paramsMap.get(key);
            if (value == null || "".equals(value)) {
                continue;
            }
            value = value.trim();
            //escape
            value = value.replaceAll("%", "\\\\%");
            value = value.replaceAll("'", "''");
            ctxMap.put(key, value);
        }
        return ctxMap;
    }

}