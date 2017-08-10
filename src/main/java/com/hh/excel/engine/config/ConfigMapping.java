package com.hh.excel.engine.config;

import java.util.*;

public class ConfigMapping {

    public static final String DATA_FORMAT_NAME_GENERAL = "General";
    public static final String DATA_FORMAT_NAME_INT = "int";
    public static final String DATA_FORMAT_NAME_DOUBLE = "double";
    public static final String DATA_FORMAT_NAME_DATE = "Date";
    public static final String DATA_FORMAT_NAME_DATE_TIME = "DateTime";
    public static final String DATA_FORMAT_NAME_ZH_DATE = "ZhDate";
    public static final String DATA_FORMAT_NAME_ZH_DATE_TIME = "ZhDateTime";
    public static final String DATA_FORMAT_NAME_STRING = "String";
    public static final String DATA_FORMAT_GENERAL = "General";
    public static final String DATA_FORMAT_INT = "0";
    public static final String DATA_FORMAT_DOUBLE = "0.00";
    public static final String DATA_FORMAT_DATE = "yyyy-MM-dd";
    public static final String DATA_FORMAT_DATE_TIME = "yyyy-MM-dd hh:mm:ss";
    public static final String DATA_FORMAT_ZH_DATE = "yyyy\"年\"MM\"月\"dd\"日\"";
    public static final String DATA_FORMAT_ZH_DATE_TIME = "yyyy\"年\"MM\"月\"dd\"日\" hh\"时\"mm\"分\"ss\"秒\"";
    public static final String DATA_FORMAT_STRING = "@";
    /**
     * 包含了 默认的 dataFormat 的 name-value 对
     */
    public static final Map<String, String> DATA_FORMAT_MAP;

    static {

        //region DATA_FORMAT_MAP 的初始化（添加元素对）
        Map<String, String> changeableMap = new HashMap<>();
        changeableMap.put(DATA_FORMAT_NAME_GENERAL, DATA_FORMAT_GENERAL);
        changeableMap.put(DATA_FORMAT_NAME_INT, DATA_FORMAT_INT);
        changeableMap.put(DATA_FORMAT_NAME_DOUBLE, DATA_FORMAT_DOUBLE);
        changeableMap.put(DATA_FORMAT_NAME_DATE, DATA_FORMAT_DATE);
        changeableMap.put(DATA_FORMAT_NAME_DATE_TIME, DATA_FORMAT_DATE_TIME);
        changeableMap.put(DATA_FORMAT_NAME_ZH_DATE, DATA_FORMAT_ZH_DATE);
        changeableMap.put(DATA_FORMAT_NAME_ZH_DATE_TIME, DATA_FORMAT_ZH_DATE_TIME);
        changeableMap.put(DATA_FORMAT_NAME_STRING, DATA_FORMAT_STRING);
        DATA_FORMAT_MAP = Collections.unmodifiableMap(changeableMap);
        //endregion

    }
}
