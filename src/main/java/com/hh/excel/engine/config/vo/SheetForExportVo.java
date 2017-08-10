package com.hh.excel.engine.config.vo;


import com.hh.excel.engine.common.Constants;

/**
 * @author : huanghao
 * @date : 2017/8/4 15:43
 */
public class SheetForExportVo extends SheetVo {
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

    public SheetForExportVo() {
        this.maxColumnWidth = Constants.MAX_COLUMN_WIDTH;
        this.maxRowHeight = Constants.MAX_ROW_HEIGHT;
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
