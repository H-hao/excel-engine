package com.hh.excel.engine.core.vo;

import com.hh.excel.engine.config.vo.ExcelEntry;
import org.apache.poi.ss.usermodel.Cell;

/**
 * 待处理的entry信息，
 * <p>这里的待处理是指：在xml中，setter/getter的表达式是由具体条件来决定的，而在获取最终应该是哪一个setter/getter时，需要计算包含的条件，而目前无法计算出这个条件，所以将其加入 pending Map 中存储，在遍历完一遍后，再循环遍历 pending Map 进行赋值
 * </p>
 *
 * @author huanghao
 * @date 2017年4月20日下午1:28:34
 */
public class PendingInfo {
    private ExcelEntry entry;
    private Object value;// 单元格的值，setter 时使用，导入
    private Cell cell;// 将值写入其中的单元格，getter 时使用，导出

    public PendingInfo() {
        super();
    }

    public PendingInfo(ExcelEntry entry, Object value) {
        super();
        this.entry = entry;
        this.value = value;
    }

    public PendingInfo(ExcelEntry entry, Cell cell) {
        this.entry = entry;
        this.cell = cell;
    }

    public ExcelEntry getEntry() {
        return entry;
    }

    public void setEntry(ExcelEntry entry) {
        this.entry = entry;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }
}
