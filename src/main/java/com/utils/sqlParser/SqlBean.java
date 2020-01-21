package com.utils.sqlParser;

/**
 * @author wwb
 * @title: SqlBean
 * @projectName aliyun-zhibo
 * @description: TODO
 * @date 2019/11/2513:43
 */
import java.util.Map;

public class SqlBean {

    private String sql;
    private Map<String, String> sqlData;
    public String getSql() {
        return sql;
    }
    public void setSql(String sql) {
        this.sql = sql;
    }
    public Map<String, String> getSqlData() {
        return sqlData;
    }
    public void setSqlData(Map<String, String> sqlData) {
        this.sqlData = sqlData;
    }

}