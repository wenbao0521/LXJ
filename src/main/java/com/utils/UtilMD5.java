package com.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
* @Description:    MD5工具类
* @Author:         YLS
* @CreateDate:     2019/11/26 17:08
*/
public class UtilMD5 {

    private static final Logger LOGGER = LoggerFactory.getLogger(UtilMD5.class);

    /**
     * @Description:获取MD5
     * @author:     YLS
     * @param:      [str]
     * @return      java.lang.String
     * @date        2019/11/20 13:06
     */
    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String md5=new BigInteger(1, md.digest()).toString(16);
            //BigInteger会把0省略掉，需补全至32位
            return fillMD5(md5);
        } catch (Exception e) {
            throw new RuntimeException("MD5加密错误:"+e.getMessage(),e);
        }
    }

    /**
     * @Description:MD5
     * @author:     YLS
     * @param:      [md5]
     * @return      java.lang.String
     * @date        2019/11/20 13:04
     */
    private static String fillMD5(String md5){
        return md5.length()==32?md5:fillMD5("0"+md5);
    }
}
