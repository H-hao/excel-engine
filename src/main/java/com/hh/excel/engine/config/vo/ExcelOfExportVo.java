package com.hh.excel.engine.config.vo;

/**
 * 封装xml配置中excelExport的信息
 *
 * @author huanghao
 * @date 2017年4月6日上午11:05:29
 */
public class ExcelOfExportVo extends ExcelVo {

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
     * TODO 是否需要？
     */
    @Deprecated
    private String title;
    /**
     * 全局引用样式
     */
    private String globalStyleRef;
    /**
     * 全局引用表头样式
     */
    private String globalHeaderStyleRef;

    public ExcelOfExportVo() {
        this.fileName = DEFAULT_EXCEL_NAME;
        this.exportType = XLSX;
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

    public String getGlobalStyleRef() {
        return globalStyleRef;
    }

    public void setGlobalStyleRef(String globalStyleRef) {
        this.globalStyleRef = globalStyleRef;
    }

    public String getGlobalHeaderStyleRef() {
        return globalHeaderStyleRef;
    }

    public void setGlobalHeaderStyleRef(String globalHeaderStyleRef) {
        this.globalHeaderStyleRef = globalHeaderStyleRef;
    }

}
