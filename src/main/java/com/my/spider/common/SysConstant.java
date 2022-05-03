package com.my.spider.common;

import java.util.HashMap;

public class SysConstant {
    //默认字符集
    public static String DEFAULT_CHARSET = "utf-8";
    //网站
    public static String BASE_URL = "https://search.jd.com/Search";

    public static HashMap<String,String> setHeader(){
        HashMap<String,String> headerMap = new HashMap<>();
        headerMap.put(":authority","search.jd.com");
        headerMap.put(":method","GET");
        headerMap.put(":scheme","https");
        headerMap.put("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        headerMap.put("accept-encoding","gzip, deflate, br");
        headerMap.put("accept-language","zh-CN,zh;q=0.9");
        headerMap.put("cache-control","max-age=0");
        headerMap.put("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
        return headerMap;
    }

    //默认日期格式
    public static String DEFAULT_DATE_FORMAT = "yyyy-MM--dd HH:mm:ss";
}
