package com.hh.excel.engine.config.vo;


import com.hh.excel.engine.common.CommonUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author : huanghao
 * @date : 2017/8/4 15:35
 */
public class SheetVo extends AbstractBaseConfig {
    /**
     * sheet 的名称；在导入时，也可以根据 name 来获取对应的 sheet，再进行导入
     */
    private String name;
    /**
     * typeName ： 要封装的类型的名称
     */
    private String type;
    /**
     * excelMapRef ： 引用的映射信息id(类似resultMap)
     */
    private String excelMapRef;
    /**
     * startRowNo : 从指定的行开始读取/设置，设置的值小于等于0时，设置为0，
     */
    private int startRowNo;
    /**
     * startCellNo : 从指定的列开始读取/设置，设置的值小于等于0时，设置为0，
     */
    private int startCellNo;
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
     * excelEntryMap : 解析每一列的配置，key为表头的名称；
     * 新增对多 sheet 的支持，此处更改 Map 泛型 updated by huanghao on 2017/8/4 14:12
     * 现：ExcelEntry 对应每一个 entry 节点，Map<String, ExcelEntry> 对应每一个 map 或 propertiesForTemplate
     * TODO 需不需要对 sheet 提供 重用 的支持？（类似 Mapping，font，style，单独提出来，并提供 id）
     */
    private Map<String, ExcelEntry> excelEntryMap;
    /**
     * excelVo 的引用，TODO 在创建 sheet 之后设置 excelVo
     */
    private ExcelVo excelVo;

    public SheetVo() {
        this.startRowNo = 0;
        this.startCellNo = 0;
        this.isConsecutive = Boolean.FALSE;
        this.excelEntryMap = new LinkedHashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExcelMapRef() {
        return excelMapRef;
    }

    public void setExcelMapRef(String excelMapRef) {
        this.excelMapRef = excelMapRef;
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

    public String getStartIndexForTemplate() {
        return startIndexForTemplate;
    }

    public void setStartIndexForTemplate(String startIndexForTemplate) {
        this.startIndexForTemplate = startIndexForTemplate;
        // this.isIndexWay = Boolean.TRUE;// 不进行关联，由 templateLocation 关联
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

    public ExcelVo getExcelVo() {
        return excelVo;
    }

    public void setExcelVo(ExcelVo excelVo) {
        this.excelVo = excelVo;
    }
}
