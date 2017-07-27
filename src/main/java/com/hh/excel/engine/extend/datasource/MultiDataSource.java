package com.hh.excel.engine.extend.datasource;

/**
 * 多数据源接口，用于在执行sql时，数据源的切换
 * Created by huanghao on 2017/6/28.
 */
public interface MultiDataSource {

    void switchTo(String destDataSource);

    void clear();
}
