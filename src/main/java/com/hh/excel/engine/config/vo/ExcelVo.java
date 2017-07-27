package com.hh.excel.engine.config.vo;

import com.hh.excel.engine.common.CommonUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelVo extends AbstractBaseConfig {
    public static final String DEFAULT_EXCEL_NAME = "新建工作表";

    /**
     * typeName ： 要封装的类型的名称
     */
    private String type;
    /**
     * startRowNo : 从指定的行开始读取/设置，设置的值小于等于0时，设置为0，
     */
    private int startRowNo;
    /**
     * startCellNo : 从指定的列开始读取/设置，设置的值小于等于0时，设置为0，
     */
    private int startCellNo;
    /**
     * excelMapRef ： 引用的映射信息id(类似resultMap)
     */
    private String excelMapRef;
    /**
     * 导入导出对应的 row 失败阈值
     */
    private int rowFailThreshold;
    /**
     * 导入导出对应的 cell 失败阈值
     */
    private int columnFailThreshold;
    /**
     * 是否是通过 index 的方式来进行映射，如果为 true，则表示是，否则为 false ；<br/>
     * 目前还有一种方式是，header ；
     */
    private Boolean isIndexWay;
    /**
     * 使用 excel 模版，此属性表示 模版文件 的位置
     */
    private String templateLocation;
    /**
     * 读取 excel 模版的开始索引
     */
    private String startIndexForTemplate;
    /**
     * 表示是否直接根据startIndexForTemplate自增的方式获取其他的有效索引<br/>
     * 如：startIndexForTemplate为 C1，那么 isConsecutive = true 时，那么会遍历 C2, C3, C4 ...
     */
    private Boolean isConsecutive;
    /**
     * 忽略的列，即不向其中设置值，要将值设置到的列向后顺延
     */
    private String ignoreColumnIndex;
    /**
     * excelEntryMap : 解析每一列的配置，key为表头的名称
     */
    private Map<String, ExcelEntry> excelEntryMap;

    public ExcelVo() {
        this.startRowNo = 0;
        this.startCellNo = 0;
        this.isIndexWay = Boolean.FALSE;
        this.isConsecutive = Boolean.FALSE;
        this.excelEntryMap = new LinkedHashMap<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStartRowNo() {
        return startRowNo;
    }

    public void setStartRowNo(String startRowNo) {
        int temp = CommonUtil.isEmpty(startRowNo) ? 0 : Integer.parseInt(startRowNo);
        this.startRowNo = temp > 0 ? temp : 0;
    }

    public int getStartCellNo() {
        return startCellNo;
    }

    public void setStartCellNo(String startCellNo) {
        int temp = CommonUtil.isEmpty(startCellNo) ? 0 : Integer.parseInt(startCellNo);
        this.startCellNo = temp > 0 ? temp : 0;
    }

    public String getExcelMapRef() {
        return excelMapRef;
    }

    public void setExcelMapRef(String excelMapRef) {
        this.excelMapRef = excelMapRef;
    }

    public int getRowFailThreshold() {
        return rowFailThreshold;
    }

    public void setRowFailThreshold(String rowFailThreshold) {
        this.rowFailThreshold = Integer.parseInt(rowFailThreshold);
    }

    public int getColumnFailThreshold() {
        return columnFailThreshold;
    }

    public void setColumnFailThreshold(String columnFailThreshold) {
        this.columnFailThreshold = Integer.parseInt(columnFailThreshold);
    }

    public Boolean getIsIndexWay() {
        return isIndexWay;
    }

    public void setIsIndexWay(String indexWay) {
        this.isIndexWay = Boolean.valueOf(indexWay);
    }

    public String getTemplateLocation() {
        return templateLocation;
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
        this.isIndexWay = Boolean.TRUE;
    }

    public String getStartIndexForTemplate() {
        return startIndexForTemplate;
    }

    public void setStartIndexForTemplate(String startIndexForTemplate) {
        this.startIndexForTemplate = startIndexForTemplate;
        this.isIndexWay = Boolean.TRUE;
    }

    public Boolean getIsConsecutive() {
        return isConsecutive;
    }

    public void setIsConsecutive(String consecutive) {
        isConsecutive = Boolean.valueOf(consecutive);
    }

    public String getIgnoreColumnIndex() {
        return ignoreColumnIndex;
    }

    public void setIgnoreColumnIndex(String ignoreColumnIndex) {
        this.ignoreColumnIndex = ignoreColumnIndex;
    }

    public Map<String, ExcelEntry> getExcelEntryMap() {
        return excelEntryMap;
    }

    // public void setExcelEntryMap(Map<String, ExcelEntry> excelEntryMap) {
    // 	this.excelEntryMap = excelEntryMap;
    // }

}
