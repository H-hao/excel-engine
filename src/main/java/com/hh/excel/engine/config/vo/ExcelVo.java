package com.hh.excel.engine.config.vo;

import java.util.LinkedList;
import java.util.List;

public class ExcelVo extends AbstractBaseConfig {
    public static final String DEFAULT_EXCEL_NAME = "新建工作表";

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
     * 一个 workbook 中对应的多个 sheetVos
     */
    private List<SheetVo> sheetVos;

    public ExcelVo() {
        this.isIndexWay = Boolean.FALSE;
        this.sheetVos = new LinkedList<>();
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

    public List<SheetVo> getSheetVos() {
        return sheetVos;
    }

    // public void setSheetVos(List<SheetVo> sheetVos) {
    //     this.sheetVos = sheetVos;
    // }

    // public void addSheetVos(SheetVo...sheetVos){
    //     this.sheetVos.addAll(Arrays.asList(sheetVos));
    // }

}
