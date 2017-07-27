package com.hh.excel.engine.config.vo;

import com.hh.excel.engine.config.mapping.ColorMapping;
import com.hh.excel.engine.config.mapping.FontMapping;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

public class FontVo extends AbstractExtendableConfig {
    private String fontName;
    private String fontHeight;
    private short fontHeightInPoints;
    private boolean italic;
    private boolean strikeout;// 水平删除线
    private short color;
    private short typeOffset;// 上下标
    private byte underline;// 下划线
    private byte charSet;// 字符集
    // private String boldweight;// 过时，由bold控制，如果存在，则会影响 bold
    private boolean bold;

    public FontVo() {
        // 默认配置
        this.fontName = "微软雅黑";
        // fontHeight// 有了 fontHeightInPoints 就可以了
        this.fontHeightInPoints = 12;
        this.italic = Boolean.FALSE;
        this.strikeout = Boolean.FALSE;
        this.color = IndexedColors.AUTOMATIC.getIndex();
        this.typeOffset = Font.SS_NONE;
        this.underline = Font.U_NONE;
        this.charSet = Font.DEFAULT_CHARSET;
        this.bold = Boolean.FALSE;
    }

    public FontVo(String id) {
        this();
        this.setId(id);
    }

    public FontVo(String id, String fontName) {
        this(id);
        this.fontName = fontName;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getFontHeight() {
        return fontHeight;
    }

    public void setFontHeight(String fontHeight) {
        this.fontHeight = fontHeight;
    }

    public short getFontHeightInPoints() {
        return fontHeightInPoints;
    }

    public void setFontHeightInPoints(String fontHeightInPoints) {
        if (Short.parseShort(fontHeightInPoints) > 0) {
            this.fontHeightInPoints = Short.parseShort(fontHeightInPoints);
        }
    }

    public boolean getItalic() {
        return italic;
    }

    public void setItalic(String italic) {
        this.italic = Boolean.parseBoolean(italic);
    }

    public boolean getStrikeout() {
        return strikeout;
    }

    public void setStrikeout(String strikeout) {
        this.strikeout = Boolean.parseBoolean(strikeout);
    }

    public short getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = ColorMapping.transIndexedColorsToShort(color);
    }

    public short getTypeOffset() {
        return typeOffset;
    }

    public void setTypeOffset(String typeOffset) {
        this.typeOffset = FontMapping.transTypeOffset(typeOffset);
    }

    public byte getUnderline() {
        return underline;
    }

    public void setUnderline(String underline) {
        this.underline = FontMapping.transUnderline(underline);
    }

    public byte getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = FontMapping.transCharSet(charSet);
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(String bold) {
        this.bold = Boolean.parseBoolean(bold);
    }

}
