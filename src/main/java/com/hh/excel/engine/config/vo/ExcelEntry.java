package com.hh.excel.engine.config.vo;

import java.util.Map;
import java.util.Set;

/**
 * 用于映射excel的 <strong>每一列</strong> 与其的配置<br>
 * tips：引入了 poi 的 font 和 cellstyle 类
 *
 * @author huanghao
 * @date 2017年4月6日上午10:22:15
 */
public class ExcelEntry {
    /**
     * header : 表头
     */
    private String header;
    /**
     * column 的索引，和 header 作用相同，
     */
    private String columnIndex;
    /**
     * 当前列是否需要有值
     */
    private Boolean required;
    /**
     * isImport : 是否是导入
     */
    private Boolean isImport;
    /**
     * 一个固定的列值；优先级最高，其次 setter，其次 setterELs；
     */
    private String value;
    /**
     * setter ：setter 方法名
     */
    private String setter;
    /**
     * setterEL : 条件获取 setter ，优先级低于 setter <br/>setterEls 形式：map&lt;condition,value&gt;
     */
    private Map<String, String> setterELs;
    /**
     * 将此表头对应的值复制到instance对应的属性下，属性可以有多个，以 “,” 分割，如：copyTo="caseInfoVo.caseNo,caseVo.caseNo"
     */
    private String copyTo;
    /**
     * getter : getter 方法名
     */
    private String getter;
    /**
     * getterEL : 条件获取getter ，优先级低于 getter <br/>getterEls 形式：map&lt;condition,value&gt;
     */
    private Map<String, String> getterELs;
    /**
     * cellType : 单元格的对应的java类型
     */
    private String cellType;
    /**
     * isBlank : 如果读取到的cell内容为 blank，则设置的属性为此指定的值
     */
    private String blank;
    /**
     * pattern : 格式化表达式，一般用于 日期 的格式化
     */
    private String pattern;
    /**
     * dataFormat : 格式化数据
     */
    private String dataFormat;
    /**
     * width : 每一列的宽度，TODO 避免 int 的默认值为 0 ？
     */
    private String width;
    /**
     * isAutoWidth : 是否自动调整列宽
     */
    private Boolean isAutoWidth;
    /**
     * dataSource : 查询 sql，要使用的数据源
     */
    private String dataSource;
    /**
     * selectSql ： 如果需要查询数据库，则需要设置此属性
     */
    private String selectSql;
    /**
     * excelEntry 对应的依赖属性，key 为 excelEntry 中 if 的 test 内容，value 为 此 test 中包含的依赖
     */
    private Map<String, Set<String>> dependencies;
    /**
     * sheetVo 的引用
     */
    private SheetVo sheetVo;

    public ExcelEntry() {
        this.required = Boolean.FALSE;
        this.isAutoWidth = Boolean.TRUE;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(String columnIndex) {
        this.columnIndex = columnIndex;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = Boolean.valueOf(required);
    }

    public Boolean getIsImport() {
        return isImport;
    }

    public void setIsImport(String isImport) {
        this.isImport = Boolean.parseBoolean(isImport);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSetter() {
        return setter;
    }

    public void setSetter(String setter) {
        this.setter = setter;
        this.isImport = Boolean.TRUE;
    }

    public Map<String, String> getSetterELs() {
        return setterELs;
    }

    public void setSetterELs(Map<String, String> setterELs) {
        this.setterELs = setterELs;
        this.isImport = Boolean.TRUE;
    }

    public String getCopyTo() {
        return copyTo;
    }

    public void setCopyTo(String copyTo) {
        this.copyTo = copyTo;
    }

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
        this.isImport = Boolean.FALSE;
    }

    public Map<String, String> getGetterELs() {
        return getterELs;
    }

    public void setGetterELs(Map<String, String> getterELs) {
        this.getterELs = getterELs;
        this.isImport = Boolean.FALSE;
    }

    public String getCellType() {
        return cellType;
    }

    public void setCellType(String cellType) {
        this.cellType = cellType;
    }

    public String getBlank() {
        return blank;
    }

    public void setBlank(String blank) {
        this.blank = blank;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
        this.isAutoWidth = Boolean.FALSE;
    }

    public Boolean getIsAutoWidth() {
        return isAutoWidth;
    }

    public void setIsAutoWidth(Boolean isAutoWidth) {
        this.isAutoWidth = isAutoWidth;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getSelectSql() {
        return selectSql;
    }

    public void setSelectSql(String selectSql) {
        this.selectSql = selectSql;
    }

    public Map<String, Set<String>> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Map<String, Set<String>> dependencies) {
        this.dependencies = dependencies;
    }

    public SheetVo getSheetVo() {
        return sheetVo;
    }

    public void setSheetVo(SheetVo sheetVo) {
        this.sheetVo = sheetVo;
    }
}
