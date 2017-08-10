package com.hh.excel.engine.core.engine;

import com.hh.excel.engine.config.vo.ExcelEntry;
import com.hh.excel.engine.config.vo.ExcelVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象的顶层 excelEngine 类，在其中做一些初步的处理
 * Created by huanghao on 2017/6/28.
 */
public abstract class AbstractExcelEngine implements ExcelEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExcelEngine.class);

    public AbstractExcelEngine() {}

    // TODO 将 DefaultExcelEngine 中一些处理抽取到此类中

    /**
     * 用于实例化 excelVo 对象后的一些处理，如，属性的迁移
     */
    @Deprecated
    protected void afterCreateExcelVo(ExcelVo excelVo){}

    /**
     * 用于实例化 excelEntry 对象后的一些处理，如，属性的迁移
     */
    @Deprecated
    protected void afterCreateExcelEntry(ExcelEntry excelEntry){}
}
