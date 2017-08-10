package com.hh.excel.engine.common;

/**
 * 常量类
 * Created by huanghao on 2017/6/28.
 */
public class Constants {

    /*-------------------------------------------
    |                 EXCEL ENGINE              |
    ============================================*/

    /*-------------------------------------------
    |                 EXCEL  XML                |
    ============================================*/
    // 各个节点名称
    public static final String ELEMENT_EXCEL_EXPORT = "excelExport";
    public static final String ELEMENT_EXCEL_IMPORT = "excelImport";
    public static final String ELEMENT_SHEET_EXPORT = "sheetForExport";
    public static final String ELEMENT_SHEET_IMPORT = "sheetForImport";
    public static final String ELEMENT_EXCEL_MAP = "excelMap";
    public static final String ELEMENT_STYLE = "style";
    public static final String ELEMENT_FONT = "font";

    /*-------------------------------------------
    |                 EXCEL CONFIG              |
    ============================================*/


    /*-------------------------------------------
    |                 EXCEL IMPORT              |
    ============================================*/
    /**
     * copyTo 配置的分隔符
     */
    public static final String REGEX_OF_COPY_TO = ",";
    /*-------------------------------------------
    |                 EXCEL EXPORT              |
    ============================================*/
    public static final int MAX_COLUMN_WIDTH = 25 * 256;// 30 个字符宽度
    public static final short MAX_ROW_HEIGHT = 36 * 12;

    /*-------------------------------------------
    |                     FONT                  |
    ============================================*/

    /*-------------------------------------------
    |                    STYLE                  |
    ============================================*/

    /*-------------------------------------------
    |                    OTHERS                 |
    ============================================*/


}
