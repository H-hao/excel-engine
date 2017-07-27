package com.hh.excel.engine.config.vo;

import com.hh.excel.engine.config.mapping.CellStyleMapping;
import com.hh.excel.engine.config.mapping.ColorMapping;
import org.apache.poi.ss.usermodel.*;

public class CellStyleVo extends AbstractExtendableConfig {
	private String dataFormat;// TODO 数据格式(workbook.createDataFormat().getFormat(dataFormat))，such as:General，@，0，0.00
	private String font;// 字体名称，其他 font 属性默认
	private String fontRef;// 引用的font配置的id
	private boolean hidden;// 设置隐藏单元格使用的样式
	private boolean locked;// 设置锁住单元格使用的样式
	private boolean wrapText;// 自动换行
	private short alignment;// 水平对齐
	private short verticalAlignment;// 垂直对齐
	private short rotation;// 旋转角度
	private short borderLeft;// 边框
	private short borderRight;
	private short borderTop;
	private short borderBottom;
	private short leftBorderColor;// 颜色，都采用 short 类型
	private short rightBorderColor;
	private short topBorderColor;
	private short bottomBorderColor;
	private short fillPattern;// 填充样式
	private short fillForegroundColor;// 填充前景色（样式的颜色）
	// private short fillBackgroundColor;// 填充背景色（底色）// FIXME 暂时取消背景色，可以使用前景色与实心样式代替
	private boolean shrinkToFit;// 自适应（Excel 单元格格式中的缩小字体填充）(auto-sized to shrink to fit if the text is too long)

	public CellStyleVo() {
		this.hidden = Boolean.FALSE;
		this.locked = Boolean.FALSE;
		this.wrapText = Boolean.FALSE;
		this.alignment = HorizontalAlignment.CENTER.getCode();
		this.verticalAlignment = VerticalAlignment.CENTER.getCode();
		this.rotation = 0;

		this.borderLeft = BorderStyle.NONE.getCode();
		this.borderRight = BorderStyle.NONE.getCode();
		this.borderTop = BorderStyle.NONE.getCode();
		this.borderBottom = BorderStyle.NONE.getCode();

		this.leftBorderColor = IndexedColors.BLACK.getIndex();
		this.rightBorderColor = IndexedColors.BLACK.getIndex();
		this.topBorderColor = IndexedColors.BLACK.getIndex();
		this.bottomBorderColor = IndexedColors.BLACK.getIndex();

		// color TODO 在点击单元格之后，会导致 单元格 变为黑色，暂时取消背景色，或者不使用 beanutils 的 copy 方法，采用手动复制属性的方式来选择设置有效的属性
		// 如果要这种效果（没有编辑单元格的时候也显示背景色，而不是只有编辑时显示背景色），则需要设置背景色和样式为实心（必须为实心，否则样式就会以前景色展示）
		this.fillForegroundColor = IndexedColors.AUTOMATIC.getIndex();
		// this.fillBackgroundColor = IndexedColors.AUTOMATIC.getIndex();
		this.fillPattern = FillPatternType.NO_FILL.getCode();
		this.shrinkToFit = Boolean.FALSE;
	}

	public CellStyleVo(String id) {
		this();
		this.setId(id);
	}

	public CellStyleVo(String id, String dataFormat, String fontRef) {
		this(id);
		this.dataFormat = dataFormat;
		this.fontRef = fontRef;
	}

	public String getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getFontRef() {
		return fontRef;
	}

	public void setFontRef(String fontRef) {
		this.fontRef = fontRef;
	}

	public boolean getHidden() {
		return hidden;
	}

	public void setHidden(String hidden) {
		this.hidden = Boolean.parseBoolean(hidden);
	}

	public boolean getLocked() {
		return locked;
	}

	public void setLocked(String locked) {
		this.locked = Boolean.parseBoolean(locked);
	}

	public short getAlignment() {
		return alignment;
	}

	public void setAlignment(String alignment) {
		this.alignment = CellStyleMapping.transAlignment(alignment).getCode();
	}

	public boolean getWrapText() {
		return wrapText;
	}

	public void setWrapText(String wrapText) {
		this.wrapText = Boolean.parseBoolean(wrapText);
	}

	public short getVerticalAlignment() {
		return verticalAlignment;
	}

	public void setVerticalAlignment(String verticalAlignment) {
		this.verticalAlignment = CellStyleMapping.transVerticalAlignment(verticalAlignment).getCode();
	}

	public short getRotation() {
		return rotation;
	}

	public void setRotation(String rotation) {
		this.rotation = Short.parseShort(rotation);
	}

	public short getBorderLeft() {
		return borderLeft;
	}

	public void setBorderLeft(String borderLeft) {
		this.borderLeft = CellStyleMapping.transBorderStyle(borderLeft).getCode();
	}

	public short getBorderRight() {
		return borderRight;
	}

	public void setBorderRight(String borderRight) {
		this.borderRight = CellStyleMapping.transBorderStyle(borderRight).getCode();
	}

	public short getBorderTop() {
		return borderTop;
	}

	public void setBorderTop(String borderTop) {
		this.borderTop = CellStyleMapping.transBorderStyle(borderTop).getCode();
	}

	public short getBorderBottom() {
		return borderBottom;
	}

	public void setBorderBottom(String borderBottom) {
		this.borderBottom = CellStyleMapping.transBorderStyle(borderBottom).getCode();
	}

	public short getLeftBorderColor() {
		return leftBorderColor;
	}

	public void setLeftBorderColor(String leftBorderColor) {
		this.leftBorderColor = ColorMapping.transIndexedColorsToShort(leftBorderColor);
	}

	public short getRightBorderColor() {
		return rightBorderColor;
	}

	public void setRightBorderColor(String rightBorderColor) {
		this.rightBorderColor = ColorMapping.transIndexedColorsToShort(rightBorderColor);
	}

	public short getTopBorderColor() {
		return topBorderColor;
	}

	public void setTopBorderColor(String topBorderColor) {
		this.topBorderColor = ColorMapping.transIndexedColorsToShort(topBorderColor);
	}

	public short getBottomBorderColor() {
		return bottomBorderColor;
	}

	public void setBottomBorderColor(String bottomBorderColor) {
		this.bottomBorderColor = ColorMapping.transIndexedColorsToShort(bottomBorderColor);
	}

	public short getFillPattern() {
		return fillPattern;
	}

	public void setFillPattern(String fillPattern) {
		this.fillPattern = CellStyleMapping.transFillPattern(fillPattern).getCode();
	}

	// public short getFillBackgroundColor() {
	// 	return fillBackgroundColor;
	// }

	// public void setFillBackgroundColor(String fillBackgroundColor) {
	// 	this.fillBackgroundColor = ColorMapping.transIndexedColorsToShort(fillBackgroundColor);
	// }

	public short getFillForegroundColor() {
		return fillForegroundColor;
	}

	public void setFillForegroundColor(String fillForegroundColor) {
		this.fillForegroundColor = ColorMapping.transIndexedColorsToShort(fillForegroundColor);
	}

	public boolean getShrinkToFit() {
		return shrinkToFit;
	}

	public void setShrinkToFit(String shrinkToFit) {
		this.shrinkToFit = Boolean.parseBoolean(shrinkToFit);
	}
	
}
