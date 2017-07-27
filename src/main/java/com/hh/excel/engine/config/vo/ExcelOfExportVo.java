package com.hh.excel.engine.config.vo;

/**
 * 封装xml配置中excelExport的信息
 *
 * @param
 * @author huanghao
 * @date 2017年4月6日上午11:05:29
 */
public class ExcelOfExportVo extends ExcelVo {
    protected static final int MAX_COLUMN_WIDTH = 25 * 256;// 30 个字符宽度
    protected static final short MAX_ROW_HEIGHT = 36 * 12;

    /**
     * fileName : 导出的文件名
     */
    private String fileName;
    /**
     * exportType ： 导出的类型
     */
    private String exportType;
    /**
     * title : 用于excel最上方展示的title
     */
    private String title;
    /**
     * freezeHeader ： 导出时，冻结顶部多少行
     */
    private int freezeTop;
    /**
     * freezeLeft :冻结左部
     */
    private int freezeLeft;
    /**
     * styleRef:引用的样式id
     */
    private String styleRef;
    /**
     * headerStyleRef : 所有cell使用的样式
     */
    private String headerStyleRef;
    /**
     * 最大单元格宽度
     */
    private int maxColumnWidth;
    /**
     * 最大行高度
     */
    private short maxRowHeight;

    public ExcelOfExportVo() {
        this.fileName = DEFAULT_EXCEL_NAME;
        this.exportType = XLSX;
        this.maxColumnWidth = MAX_COLUMN_WIDTH;
        this.maxRowHeight = MAX_ROW_HEIGHT;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getFreezeTop() {
        return freezeTop;
    }

    public void setFreezeTop(String freezeTop) {
        this.freezeTop = Integer.parseInt(freezeTop);
    }

    public int getFreezeLeft() {
        return freezeLeft;
    }

    public void setFreezeLeft(String freezeLeft) {
        this.freezeLeft = Integer.parseInt(freezeLeft);
    }

    public String getStyleRef() {
        return styleRef;
    }

    public void setStyleRef(String styleRef) {
        this.styleRef = styleRef;
    }

    public String getHeaderStyleRef() {
        return headerStyleRef;
    }

    public void setHeaderStyleRef(String headerStyleRef) {
        this.headerStyleRef = headerStyleRef;
    }

    public int getMaxColumnWidth() {
        return maxColumnWidth;
    }

    public void setMaxColumnWidth(String maxColumnWidth) {
        this.maxColumnWidth = Integer.parseInt(maxColumnWidth);
    }

    public short getMaxRowHeight() {
        return maxRowHeight;
    }

    public void setMaxRowHeight(String maxRowHeight) {
        this.maxRowHeight = Short.parseShort(maxRowHeight);
    }

}
