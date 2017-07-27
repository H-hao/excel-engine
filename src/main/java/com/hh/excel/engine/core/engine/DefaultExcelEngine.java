package com.hh.excel.engine.core.engine;

import com.hh.excel.engine.common.CommonUtil;
import com.hh.excel.engine.common.Constants;
import com.hh.excel.engine.common.CoreUtil;
import com.hh.excel.engine.config.ConfigMapping;
import com.hh.excel.engine.config.EngineConfiguration;
import com.hh.excel.engine.config.ExcelConfiguration;
import com.hh.excel.engine.config.vo.*;
import com.hh.excel.engine.core.vo.PendingInfo;
import com.hh.excel.engine.exception.OutOfColumnThresholdException;
import com.hh.excel.engine.exception.OutOfRowThresholdException;
import com.hh.excel.engine.exception.RequireException;
import com.hh.excel.engine.extend.datasource.MultiDataSource;
import com.hh.excel.engine.extend.template.DefaultTemplateManager;
import com.hh.excel.engine.extend.template.TemplateManager;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 依赖于：
 * <li>DynamicDataSource.setCustomerType（取消 DataSource 的配置）
 * <li>mybatis（使用的是 jdbc，不依赖 mybatis）
 *
 * @author huanghao
 * @date 2017年4月10日下午5:28:58
 */
public class DefaultExcelEngine extends AbstractExcelEngine implements ExcelEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExcelEngine.class);
    private static final String HEADER_STYLE = "headerStyle";// cellstylemap中表头的样式
    private static final String DATA_STYLE_PREFIX = "dataStyleWith";// cellstylemap中表头的样式
    private EngineConfiguration engineConfiguration;// 这是针对 engine 配置的对象
    protected Map<String, AbstractBaseConfig> baseConfigs;

    public DefaultExcelEngine(EngineConfiguration engineConfiguration, ExcelConfiguration excelConfiguration) {
        this.engineConfiguration = engineConfiguration;
        this.baseConfigs = excelConfiguration.getBaseConfigs();
    }

    @Override
    public void exportExcel(OutputStream outputStream, String mapperId, List<? extends Object> data) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("导出 Excel (mapperId=[{}], data.size=[{}])", mapperId, data.size());
        }
        CommonUtil.assertArgumentNotEmpty(mapperId, "必须设置mapper文件中对应的id值：" + mapperId);
        CommonUtil.assertArgumentNotNull(outputStream, "未正确读取到流：" + outputStream);
        ExcelOfExportVo exportVo = (ExcelOfExportVo) baseConfigs.get(mapperId);
        CommonUtil.assertArgumentNotNull(exportVo, "未从配置中获取到指定mapperId('" + mapperId + "')的配置对象");
        if (Boolean.TRUE.equals(exportVo.getIsIndexWay())) {
            // 模版导出
            doExportExcelWithTemplate(outputStream, data, exportVo);
        } else {
            // 非模版导出
            doExportExcel(outputStream, data, exportVo);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("导出 Excel 完成 (mapperId=[{}], data.size=[{}])", mapperId, data.size());
        }
    }

    /**
     * 在预处理之后进行导出主流程，只需要添加数据；注意：这是模版的导出方式
     *
     * @param outputStream
     * @param data
     * @param exportVo
     * @author huanghao
     * @date 2017年4月25日下午4:54:52
     */
    private void doExportExcelWithTemplate(OutputStream outputStream, List<? extends Object> data, ExcelOfExportVo exportVo) {
        // 通过接口来获取 模版文件
        String templateLocation = exportVo.getTemplateLocation();
        Workbook workbook = null;
        try {
            TemplateManager templateManager = engineConfiguration.getTemplateManager();
            if (templateLocation == null) {
                templateManager = new DefaultTemplateManager();
                engineConfiguration.setTemplateManager(templateManager);
            }
            InputStream templateInputStream = templateManager.getTemplateInputStream(templateLocation);
            workbook = WorkbookFactory.create(templateInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            CommonUtil.throwArgument("模版文件没找到：" + templateLocation);
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
        // TODO 目前使用一个 sheet
        assertWorkbook(workbook);
        Map<String, CellStyle> mappedCellStyle = createMappedCellStyle(workbook, exportVo);
        // for (Sheet sheet : workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        addDataWithTemplate(sheet, mappedCellStyle, exportVo, data);
        // }
        // 输出为 outputStream
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CommonUtil.closeAutoCloseable(workbook);
        }
    }

    /**
     * 在预处理之后进行导出主流程
     *
     * @param outputStream
     * @param data
     * @param exportVo
     * @author huanghao
     * @date 2017年4月25日下午4:54:52
     */
    private void doExportExcel(OutputStream outputStream, List<? extends Object> data, ExcelOfExportVo exportVo) {
        Workbook workbook = createWorkbook(exportVo.getExportType());
        // TODO 目前只支持单个sheet
        Sheet sheet = workbook.createSheet();
        // -------------------- 根据配置生成所有涉及到的 cellStyle/Font ----------------------
        // 因为 font cellStyle 与 WorkBook 相关，所以必须在导出的时候实例化 font cellStyle，根据配置实例化需要的 font cellStyle，但是默认的必须生成
        Map<String, CellStyle> mappedCellStyle = createMappedCellStyle(workbook, exportVo);
        // -------------------- 添加内容到 excel ----------------------
        int rowNum = exportVo.getStartRowNo();// 行号
        // 表头
        rowNum = addHeader(sheet, mappedCellStyle.get(HEADER_STYLE), exportVo, rowNum);
        sheet.createFreezePane(exportVo.getFreezeLeft(), rowNum > exportVo.getFreezeTop() ? rowNum : exportVo.getFreezeTop());
        // 数据
        addData(sheet, mappedCellStyle, exportVo, data, rowNum);

        applyConfig(sheet, exportVo);

        // 输出为 outputStream
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CommonUtil.closeAutoCloseable(workbook);
        }
    }

    private int addHeader(Sheet sheet, CellStyle headerStyle, ExcelOfExportVo exportVo, int rowNum) {
        // add header
        Row header = sheet.createRow(rowNum++);
        int cellNum = 0;// 列号
        for (Entry<String, ExcelEntry> entry : exportVo.getExcelEntryMap().entrySet()) {
            Cell cell = header.createCell(cellNum++);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(entry.getValue().getHeader());
        }
        return rowNum;
    }

    /**
     * 用在 使用模版的情况
     *
     * @param sheet
     * @param dataCellStyleMap
     * @param exportVo
     * @param data
     */
    private void addDataWithTemplate(Sheet sheet, Map<String, CellStyle> dataCellStyleMap, ExcelOfExportVo exportVo, List<?> data) {
        // 是否需要 断言 sheet
        int rowFailCount = 0;
        // 找到定位点
        String startIndex = exportVo.getStartIndexForTemplate();
        int rowIndex = CoreUtil.getRowIndexFormExcelLocation(startIndex);
        List<Integer> ignores = CommonUtil.getIgnoreColumnIndex(exportVo.getIgnoreColumnIndex());
        // 添加数据
        for (Object datum : data) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            try {
                int cellFailCount = 0;
                for (Entry<String, ExcelEntry> entry : exportVo.getExcelEntryMap().entrySet()) {
                    ExcelEntry excelEntry = entry.getValue();
                    int cellNum = Integer.parseInt(excelEntry.getColumnIndex());
                    if (ignores.contains(cellNum)) {
                        continue;
                    }
                    Cell cell = row.getCell(cellNum);
                    if (cell == null) {
                        cell = row.createCell(cellNum);
                    }
                    try {
                        Object value = getValueFormInstance(datum, excelEntry);
                        setCell(cell, value, dataCellStyleMap, excelEntry);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ParseException e) {
                        e.printStackTrace();
                        cellFailCount = increaseColumnAndThrowIfPossible(cellFailCount, exportVo.getColumnFailThreshold());
                    }
                }
            } catch (OutOfColumnThresholdException e) {
                e.printStackTrace();
                rowFailCount = increaseRowAndThrowIfPossible(rowFailCount, exportVo.getRowFailThreshold());
            }
            rowIndex++;
        }
    }

    /**
     * 用在不使用模版的情况下
     *
     * @param sheet
     * @param dataCellStyleMap
     * @param exportVo
     * @param data
     * @param rowNum
     */
    private void addData(Sheet sheet, Map<String, CellStyle> dataCellStyleMap, ExcelOfExportVo exportVo, List<? extends Object> data, int rowNum) {
        int rowFailCount = 0;
        for (Object thisData : data) {
            Row row = sheet.createRow(rowNum++);
            try {
                int cellFailCount = 0;
                int cellNum = 0;
                for (Entry<String, ExcelEntry> entry : exportVo.getExcelEntryMap().entrySet()) {
                    Cell cell = row.createCell(cellNum++);
                    ExcelEntry excelEntry = entry.getValue();
                    try {
                        Object value = getValueFormInstance(thisData, excelEntry);
                        setCell(cell, value, dataCellStyleMap, excelEntry);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ParseException e) {
                        // 设置值失败
                        e.printStackTrace();
                        cellFailCount = increaseColumnAndThrowIfPossible(cellFailCount, exportVo.getColumnFailThreshold());
                    }
                }
            } catch (OutOfColumnThresholdException e) {
                e.printStackTrace();
                rowFailCount = increaseRowAndThrowIfPossible(rowFailCount, exportVo.getRowFailThreshold());
            }
        }
    }

    private Object getValueFormInstance(Object thisData, ExcelEntry excelEntry) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        CommonUtil.assertArgumentNotNull(thisData, "对象不能为 null");
        CommonUtil.assertArgumentNotNull(excelEntry, "excelEntry 对象不能为 null");
        Object value = null;
        // 获取 getter（如果是 ELs 就进行转换）
        String getter = excelEntry.getGetter();
        if (CommonUtil.isEmpty(getter)) {
            // 获取 ELs 再转换
            getter = getGetterFormEls(thisData, excelEntry.getGetterELs());
        }
        if (getter != null) {
            // 获取值
            try {
                value = PropertyUtils.getProperty(thisData, getter);
            } catch (NestedNullException e) {
                if (!e.getMessage().contains("Null property value for")) {
                    CommonUtil.throwArgument("获取属性(" + getter + ")时出现异常(" + e.getMessage() + ")");
                }
            }
        }
        // 其他处理

        // 处理 selectSql
        String dataSource = excelEntry.getDataSource();
        String selectSql = excelEntry.getSelectSql();
        if (CommonUtil.isNotEmpty(selectSql)) {
            value = getValueFromSelectSqlConfig(dataSource, selectSql, value);
        }
        return value;
    }

    private String getGetterFormEls(Object thisData, Map<String, String> getterELs) {
        String getter = null;
        for (Entry<String, String> entry : getterELs.entrySet()) {
            String test = entry.getKey();// 条件：if 的 test 值
            String text = entry.getValue();// 值：if 标签节点的内容
            if (CommonUtil.isEmpty(test) || CommonUtil.isEmpty(text)) {
                continue;
            }
            try {
                if (parseCondition(thisData, test)) {
                    getter = text;
                    break;
                }
            } catch (OgnlException e) {
                e.printStackTrace();
                CommonUtil.throwArgument("解析表达式出错，expression=[" + test + "]");
            }
        }
        return getter;
    }

    /**
     * 最简单的设置 cell，但是一般的设置值，也应该附带基本的 dataFormat，所以这个方法可以不用
     *
     * @param cell
     * @param value
     * @param excelEntry
     */
    @Deprecated
    private void setCell(Cell cell, Object value, ExcelEntry excelEntry) {
        if (value != null) {
            Class<? extends Object> dataClass = value.getClass();
            if (Number.class.equals(dataClass.getGenericSuperclass())) {
                cell.setCellValue(Double.valueOf(value.toString()));
            } else if (Date.class.equals(dataClass)) {
                cell.setCellValue((Date) value);
            } else {
                cell.setCellValue(value.toString());
            }
        }
    }

    private void setCell(Cell cell, Object value, Map<String, CellStyle> cellStyleMap, ExcelEntry excelEntry)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ParseException {
        if (value != null) {
            // 如果cell有对应的数据才进行操作，否则直接跳过
            Class<? extends Object> dataClass = value.getClass();
            // 缺省会使用 double 和 datetime 的 dataFormat
            String dataFormat = excelEntry.getDataFormat();
            if (Number.class.equals(dataClass.getGenericSuperclass())) {
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue(Double.valueOf(value.toString()));
                CellStyle cellStyle = cellStyleMap.get(DATA_STYLE_PREFIX + (CommonUtil.isEmpty(dataFormat) ? ConfigMapping.DATA_FORMAT_NAME_DOUBLE : dataFormat));
                cell.setCellStyle(cellStyle);
            } else if (Date.class.equals(dataClass)) {
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue((Date) value);
                CellStyle cellStyle = cellStyleMap.get(DATA_STYLE_PREFIX + (CommonUtil.isEmpty(dataFormat) ? ConfigMapping.DATA_FORMAT_NAME_DATE_TIME : dataFormat));
                cell.setCellStyle(cellStyle);
            } else {
                // 默认都是 string 类型
                // TODO 判断类型，整数，小数,日期，其他（字符串） 是否需要？
                // if (value.toString().matches("^[-|\\+]?[\\d]+$")) {// 整数
                // cell.setCellType(CellType.STRING);
                // cell.setCellValue(value.toString());
                // cell.setCellStyle(customCellStyle != null ? customCellStyle: cellStyleMap.get(ConfigMapping.DATA_FORMAT_NAME_INT));
                // } else if
                // (value.toString().matches("^[-|\\+]?\\d+.\\d+$")) {// 小数
                // cell.setCellType(CellType.NUMERIC);
                // cell.setCellValue(Double.parseDouble(value.toString()));
                // cell.setCellStyle(cellStyleMap.get(ConfigMapping.DATA_FORMAT_NAME_DOUBLE));
                // } else {
                cell.setCellType(CellType.STRING);
                cell.setCellValue(value.toString());
                CellStyle cellStyle = cellStyleMap.get(DATA_STYLE_PREFIX + (CommonUtil.isEmpty(dataFormat) ? ConfigMapping.DATA_FORMAT_NAME_STRING : dataFormat));
                cell.setCellStyle(cellStyle);
                // }
            }
        }
    }


    /**
     * 创建 Workbook 实例，默认为 XSSFWorkbook 类型
     *
     * @param exportType
     * @return
     */
    private Workbook createWorkbook(String exportType) {
        // TODO 海量数据时，使用 SXSSFWorkbook，如何完成 当前方法；
        // (http://blog.csdn.net/little_stars/article/details/8266262)
        // 当写入数据量超过5万条以上时，很容易报错“内存溢出”，就算调整JVM的xmx为 “2048MB”，也无效果
        switch (exportType) {
            case ExcelVo.XLS:
                return new HSSFWorkbook();
            default:
                return new XSSFWorkbook();
        }
    }


    /**
     * 应用 xml 中的一些配置，包含了：页眉页脚，列宽行高
     *
     * @param sheet
     * @param exportVo
     */
    private void applyConfig(Sheet sheet, ExcelOfExportVo exportVo) {
        applyPrint(sheet, exportVo);

        // 应用 宽度和高度 的配置
        applySize(sheet, exportVo);

    }

    /**
     * 应用 xml 中与 打印 相关的配置
     *
     * @param sheet
     * @param exportVo
     */
    private void applyPrint(Sheet sheet, ExcelOfExportVo exportVo) {

        //region 页眉与页脚(打印预览中可以查看) 的设置
        /*Footer footer = sheet.getFooter();
        footer.setCenter("Center footer");
        footer.setLeft("Left footer");
        footer.setRight(HSSFFooter.font("Stencil-Normal", "Italic") + HSSFFooter.fontSize((short) 16) + "Right w/ S ");

        Header header = sheet.getHeader();
        header.setCenter("Center Header");
        header.setLeft("Left Header");
        header.setRight(HSSFHeader.font("Stencil-Normal", "Italic") + HSSFHeader.fontSize((short) 16) + "Right w/  ");*/
        //endregion


    }

    private void applySize(Sheet sheet, ExcelOfExportVo exportVo) {
        // 设置默认 列宽 行高 到 sheet 上

        //region 宽度 的设置
        Row row = sheet.getRow(0);
        if (row != null) {
            short lastCellNum = row.getLastCellNum();
            for (int i = 0; i < lastCellNum; i++) {
                ExcelEntry excelEntry = exportVo.getExcelEntryMap().get(row.getCell(i).getStringCellValue());
                if (excelEntry != null) {
                    // 宽度设置的流程 width(continue) -> autoWidth -> maxWidth
                    String width = excelEntry.getWidth();
                    if (CommonUtil.isNotEmpty(width)) {
                        sheet.setColumnWidth(i, Integer.parseInt(width));
                        continue;
                    }
                    Boolean isAutoWidth = excelEntry.getIsAutoWidth();
                    if (Boolean.TRUE.equals(isAutoWidth)) {
                        // This process can be relatively slow on large sheets, so this should normally only be called once per column, at the end of your processing.
                        // 如果列中包含了中文字符，则此方法支持并不好
                        sheet.autoSizeColumn((short) i);
                    }
                    int columnWidth = sheet.getColumnWidth(i);
                    // 判断最大宽度
                    int maxColumnWidth = exportVo.getMaxColumnWidth();
                    if (columnWidth > maxColumnWidth) {
                        sheet.setColumnWidth(i, maxColumnWidth);
                    }
                }
            }
        }
        //endregion

        //region 高度的设置
        // row.setHeight();
        //endregion
    }

    /**
     * 根据 配置的导出对象，创建其引用的 Cellstyle 的 map 集合 ，尽量只创建需要的 cellStyle和font
     *
     * @param workbook
     * @param exportVo
     * @return
     * @author huanghao
     * @date 2017年4月27日上午9:10:35
     */
    private Map<String, CellStyle> createMappedCellStyle(Workbook workbook, ExcelOfExportVo exportVo) {
        assertWorkbook(workbook);
        // 映射成功的缓存集合
        Map<String, Font> mappedFont = new HashMap<>();// 已根据xml中的配置映射的 Font 集合
        Map<String, CellStyle> mappedCellStyle = new HashMap<>();// 已根据xml中的配置映射的 CellStyle 集合

        // 完善font和CellStyle的映射顺序，并提供一个集合来存放CellStyle，mappedFont应该只作为一个缓存map，
        // 因为 font 的体现是 CellStyle，导出时，直接使用的也是CellStyle
        // 流程：首先扫描需要使用的font和cellStyle，再映射默认的font和CellStyle，再根据CellStyle中引入的font，进行映射font，映射CellStyle，并将他们分别存入相应的map中

        // 还是只有根据依赖来实例化 font cellStyle，因为就是实例化了所有的默认 font 和 cellStyle，也需要根据依赖来实例化 data 和 header 的 cellStyle
        String styleRef = exportVo.getStyleRef();// cellStyle for data
        String headerStyleRef = exportVo.getHeaderStyleRef();// cellStyle for header
        Set<String> dataFormatSet = new HashSet<>();// custom data format
        for (Entry<String, ExcelEntry> entry : exportVo.getExcelEntryMap().entrySet()) {
            String format = entry.getValue().getDataFormat();
            // 要不要排除？默认的配置到底怎么处理，是每次都创建，还是只保留配置，每次通过调用方法获取？
            // 采用 保留配置的方式
            // if (!ConfigMapping.DATA_FORMAT_MAP.contains(format)) {
            if (CommonUtil.isNotEmpty(format)) {
                dataFormatSet.add(format);// 所有都添加进来
            }
        }

        // 根据 styleRef 与 headerStyleRef 创建 cellStyle（如果后期添加其他的 cellStyle 属性，则这里就还需要添加创建 cellStyle 的代码）
        createHeaderCellStyle(mappedCellStyle, mappedFont, workbook, headerStyleRef);// 创建 header style

        createDataCellStyle(mappedCellStyle, mappedFont, workbook, styleRef, dataFormatSet);// 创建 data style

        return mappedCellStyle;
    }

    /**
     * 创建当前 workbook 对应的表头样式（headerStyle），并添加到Map中
     *
     * @param workbook
     * @param headerStyleRef
     */
    private void createHeaderCellStyle(Map<String, CellStyle> mappedCellStyle, Map<String, Font> mappedFont,
                                       Workbook workbook, String headerStyleRef) {
        CellStyle cellStyle;
        if (CommonUtil.isEmpty(headerStyleRef)) {
            // 获取默认的 headerStyle
            CellStyleVo headerStyleVo = (CellStyleVo) baseConfigs.get(ExcelConfiguration.DEFAULT_HEADER_STYLE);
            cellStyle = createCellStyleByCellStyleVo(workbook, headerStyleVo, mappedCellStyle, mappedFont);
        } else {
            CellStyleVo headerStyleVo = (CellStyleVo) baseConfigs.get(headerStyleRef);
            CommonUtil.assertArgumentNotNull(headerStyleVo, "未找到对应的cellStyle(headerStyleRef='" + headerStyleRef + "')");
            // 创建 CellStyle
            cellStyle = createCellStyleByCellStyleVo(workbook, headerStyleVo, mappedCellStyle, mappedFont);
        }
        mappedCellStyle.put(HEADER_STYLE, cellStyle);
    }

    /**
     * 创建当前 workbook 对应的数据样式（dataStyle），并添加到Map中，每一个 dataFormat 对应一个 cellStyle，如果没有任何的 format，则默认使用 general
     *
     * @param workbook
     * @param styleRef
     * @param dataFormats
     */
    private void createDataCellStyle(Map<String, CellStyle> mappedCellStyle, Map<String, Font> mappedFont,
                                     Workbook workbook, String styleRef, Set<String> dataFormats) {
        assertWorkbook(workbook);
        // 关联 dataFormat，注意：每一个 dataFormat 都需要关联一个新的 cellStyle，只有最后一次设置的 dataFormat 才有效
        // 添加缺省会使用的 dataFormat
        dataFormats.add(ConfigMapping.DATA_FORMAT_NAME_STRING);
        dataFormats.add(ConfigMapping.DATA_FORMAT_NAME_DOUBLE);
        dataFormats.add(ConfigMapping.DATA_FORMAT_NAME_DATE_TIME);
        for (String dataFormat : dataFormats) {
            CellStyle cellStyle;
            if (CommonUtil.isEmpty(styleRef)) {
                // 获取默认的 dataStyle
                CellStyleVo cellStyleVo = (CellStyleVo) baseConfigs.get(ExcelConfiguration.DEFAULT_CELL_STYLE);
                cellStyle = createCellStyleByCellStyleVo(workbook, cellStyleVo, mappedCellStyle, mappedFont);
            } else {
                CellStyleVo cellStyleVo = (CellStyleVo) baseConfigs.get(styleRef);
                cellStyle = createCellStyleByCellStyleVo(workbook, cellStyleVo, mappedCellStyle, mappedFont);
            }
            // 处理 dataFormat
            if (CommonUtil.isNotEmpty(dataFormat)) {
                if (ConfigMapping.DATA_FORMAT_MAP.containsKey(dataFormat)) {
                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat(ConfigMapping.DATA_FORMAT_MAP.get(dataFormat)));
                } else {
                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat(dataFormat));
                }
            }
            mappedCellStyle.put(DATA_STYLE_PREFIX + dataFormat, cellStyle);
        }
    }

    /**
     * @param workbook
     * @param cellStyleRef
     * @param defaultCellStyle 如果没有配置，那么就将src设置为此cellStyle
     * @return
     * @author huanghao
     * @date 2017年4月11日下午5:40:23
     */
    private CellStyle createCellStyle(Workbook workbook, String cellStyleRef, Map<String, CellStyle> mappedCellStyle,
                                      Map<String, Font> mappedFont, CellStyle defaultCellStyle) {
        assertWorkbook(workbook);
        CellStyle destCellStyle = workbook.createCellStyle();
        CellStyle SrcCellStyle;
        if (CommonUtil.isEmpty(cellStyleRef)) {
            SrcCellStyle = defaultCellStyle;
        } else {
            SrcCellStyle = createCellStyleByCellStyleVo(workbook, (CellStyleVo) baseConfigs.get(cellStyleRef), mappedCellStyle, mappedFont);
            CommonUtil.assertArgumentNotNull(SrcCellStyle, "未找到对应的cellStyle(cellStyleRef='" + cellStyleRef + "')");
        }
        // 属性迁移
        try {
            BeanUtils.copyProperties(destCellStyle, SrcCellStyle);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return destCellStyle;
    }

    /**
     * 根据 cellStylevo 对象 ，生成 cellStyle 对象
     *
     * @param cellStyleVo
     * @return
     * @author huanghao
     * @date 2017年4月25日下午3:08:07
     */
    private CellStyle createCellStyleByCellStyleVo(Workbook workbook, CellStyleVo cellStyleVo,
                                                   Map<String, CellStyle> mappedCellStyle, Map<String, Font> mappedFont) {
        assertWorkbook(workbook);
        CommonUtil.assertArgumentNotNull(cellStyleVo, "映射生成 POI 对象 CellStyle 的源 cellStyleVo 为：" + cellStyleVo);
        CellStyle cellStyle = workbook.createCellStyle();
        try {
            // TODO 这里复制的时候，会自动将值转换为其他类型，尽量将 vo 中的属性类型都更改为 short（避免copy时自动转换类型出现不想要的值），手动映射String到short
            BeanUtils.copyProperties(cellStyle, cellStyleVo);
            //region 使用 beanutils 的 copy 方法，因为这里复制会报错，方法参数类型不一样，TODO 因为对象的值为null（由于vo类型为String），导致调用method失败
            // try {
            //     PropertyUtils.copyProperties(cellStyle, cellStyleVo);
            // } catch (NoSuchMethodException e) {
            //     e.printStackTrace();
            // }
            //endregion

            // 手动复制其他复杂属性
            Font font = getFontByCellStyleVo(workbook, cellStyleVo, mappedFont);
            cellStyle.setFont(font);
            // 添加到 mappedCellStyle 中
            // mappedCellStyle.put(cellStyleVo.getId(), cellStyle);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            CommonUtil.throwArgument("将 CellStyleVo 中的属性映射到 POI 的对象 CellStyle 失败");
        }
        return cellStyle;
    }

    /**
     * 根据xml中配置的fontVo，将其映射为 poi 中的 font<br>
     * <li>如果 mappedFont 中已存在，则直接从其中获取font，否则就根据引用的font创建一个新的font
     * <li>如果直接使用的字体名称，则返回一个新的font，fontId为cellstyle的id+fontName(还是采取一定的命名规则，
     * 这样还可以供其他cellstyle使用)
     *
     * @param cellStyleVo
     * @param mappedFont
     * @return
     * @author huanghao
     * @date 2017年4月25日下午5:02:14
     */
    private Font getFontByCellStyleVo(Workbook workbook, CellStyleVo cellStyleVo, Map<String, Font> mappedFont) {
        Font font;
        String fontRef = cellStyleVo.getFontRef();
        String fontName = cellStyleVo.getFont();
        // 流程：fontRef(查询 mappedFont，再创建) -> fontName(同) -> defaultFont(同)
        if (CommonUtil.isNotEmpty(fontRef)) {// 引用其他 font
            font = mappedFont.get(fontRef);
            if (font != null) {
                return font;
            }
            // 根据fontVo进行映射
            FontVo fontVo = (FontVo) baseConfigs.get(fontRef);
            CommonUtil.assertArgumentNotNull(fontVo, "引用了未配置的 font('" + fontRef + "')");
            font = createAndPutMappedFontByFontVo(workbook, fontVo, mappedFont);
        } else if (CommonUtil.isNotEmpty(fontName)) {// 只配置 fontName
            String mappedFontKey = getMappedFontKey(fontName);
            font = mappedFont.get(mappedFontKey);
            if (font != null) {
                return font;
            }
            // 创建新的font，再修改名字
            FontVo fontVo = new FontVo(mappedFontKey, fontName);// 因为这里已经从 mappedFont 中查询过了，所以每次都 new 没有问题
            font = createAndPutMappedFontByFontVo(workbook, fontVo, mappedFont);
        } else {// 默认字体
            font = mappedFont.get(ExcelConfiguration.DEFAULT_FONT);
            if (font != null) {
                return font;
            }
            FontVo fontVo = (FontVo) baseConfigs.get(ExcelConfiguration.DEFAULT_FONT);
            CommonUtil.assertArgumentNotNull(fontVo, "引用了未配置的 font('" + ExcelConfiguration.DEFAULT_FONT + "')");
            font = createAndPutMappedFontByFontVo(workbook, fontVo, mappedFont);
        }
        return font;
    }

    /**
     * 根据 fontVo 直接映射 font，并存入 mappedFont 集合中
     *
     * @param workbook
     * @param fontVo
     * @param mappedFont
     * @return
     * @author huanghao
     * @date 2017年4月25日下午5:30:44
     */
    private Font createAndPutMappedFontByFontVo(Workbook workbook, FontVo fontVo, Map<String, Font> mappedFont) {
        assertWorkbook(workbook);
        Font font = workbook.createFont();
        try {
            BeanUtils.copyProperties(font, fontVo);
            // 手动复制复杂属性（暂无）

            // 存入 map
            mappedFont.put(fontVo.getId(), font);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return font;
    }

    /**
     * 获取存在mappedFont 中的key
     *
     * @param fontName
     * @return
     * @author huanghao
     * @date 2017年4月26日下午5:32:27
     */
    private String getMappedFontKey(String fontName) {
        return "defaultFontName:" + fontName;
    }

    @Override
    public List<Object> importExcel(InputStream inputStream, String mapperId) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("导入 Excel");
        }
        CommonUtil.assertArgumentNotEmpty(mapperId, "必须设置mapper文件中对应的id值：" + mapperId);
        CommonUtil.assertArgumentNotNull(inputStream, "未正确读取到流：" + inputStream);
        List<Object> resultList = new ArrayList<>();
        ExcelOfImportVo importVo = (ExcelOfImportVo) baseConfigs.get(mapperId);
        CommonUtil.assertArgumentNotNull(importVo, "未从配置中获取到指定mapperId('" + mapperId + "')的配置对象");
        if (Boolean.TRUE.equals(importVo.getIsIndexWay())) {
            // 索引方式导入，即：模版方式
            doImportExcelWithTemplate(inputStream, importVo, resultList);
        } else {
            // 非索引方式导入
            doImportExcel(inputStream, importVo, resultList);
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("导入 Excel 完成, list.size=[{}]", resultList.size());
        }
        return resultList;
    }

    private void doImportExcelWithTemplate(InputStream inputStream, ExcelOfImportVo importVo, List<Object> resultList) {
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            // TODO 暂时只有一个sheet
            for (Sheet sheet : workbook) {
                // try {
                // 处理 startIndex
                String startIndexForTemplate = importVo.getStartIndexForTemplate();
                int rowIndex = CoreUtil.getRowIndexFormExcelLocation(startIndexForTemplate);
                Class<? extends Object> clz = CommonUtil.getClassInstance(importVo.getType());
                Map<String, ExcelEntry> excelEntryMap = importVo.getExcelEntryMap();
                List<Integer> ignores = CommonUtil.getIgnoreColumnIndex(importVo.getIgnoreColumnIndex());
                int rowFailCount = 0;
                // 继续处理表中的数据
                for (int rowIx = rowIndex; rowIx < sheet.getLastRowNum(); rowIx++) {
                    Row row = sheet.getRow(rowIx);
                    // 跳过 startIndex 之前的部分
                    if (row == null || row.getRowNum() < rowIndex) {
                        continue;
                    }
                    int columnIndex = CoreUtil.getColumnIndexFormExcelLocation(startIndexForTemplate);
                    try {
                        Object t = clz.newInstance();
                        readRow2ObjWithTemplate(importVo, excelEntryMap, ignores, row, columnIndex, t);
                        resultList.add(t);
                    } catch (InstantiationException | IllegalAccessException | OutOfColumnThresholdException e) {
                        e.printStackTrace();// newInstance 异常
                        rowFailCount = increaseRowAndThrowIfPossible(rowFailCount, importVo.getRowFailThreshold());
                    }
                }
                // } catch (OutOfRowThresholdException e) {
                //     e.printStackTrace();
                // TODO ，暂时不做处理 sheet 的 失败策略，其他方法都也还没有做
                // }
            }
        } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    private void readRow2ObjWithTemplate(ExcelOfImportVo importVo, Map<String, ExcelEntry> excelEntryMap, List<Integer> ignores, Row row, int columnIndex, Object t) throws IllegalAccessException, InstantiationException {
        int cellFailCount = 0;
        Map<String, PendingInfo> pendingMap = new HashMap<>();
        // propertyValued 存储的是 被赋值了的 property
        Set<String> propertyValued = new HashSet<>();
        // 第一次遍历（边读取excel，边设置值）
        for (int colIx = columnIndex; colIx < row.getLastCellNum(); colIx++) {
            Cell cell = row.getCell(colIx);
            // 根据索引获取 cell 的方式，可能 cell 为 null
            if (cell == null || ignores.contains(colIx)) {
                continue;
            }
            try {
                ExcelEntry excelEntry = excelEntryMap.get(String.valueOf(colIx));// 表头每一列对应的 entry
                Object value = getValueFromCell(cell, excelEntry);
                checkRequired(excelEntry, cell, value);// 检查是否配置了 必需，与当前 value 是否为 null
                setValue2Property(t, value, excelEntry, pendingMap, propertyValued, null);
            } catch (SecurityException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                // 没找到该方法或设置值失败，就跳过
                cellFailCount = increaseColumnAndThrowIfPossible(cellFailCount, importVo.getColumnFailThreshold());
            }
        }
        // 待处理的entry集合
        if (!pendingMap.isEmpty()) {
            // 处理待处理的 entry 集合
            cellFailCount = processPendingOfSetter(t, pendingMap, propertyValued, cellFailCount);
            // 检查是否所有的待处理 entry 集合都被处理完成
            checkAllPendingProcessed(pendingMap);
        }
    }

    private void doImportExcel(InputStream inputStream, ExcelOfImportVo importVo, List<Object> resultList) {
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            // TODO 暂时只有一个sheet
            for (Sheet sheet : workbook) {
                int rowFailCount = 0;
                Iterator<Row> rowIterator = sheet.rowIterator();
                // （使用迭代器和elements能够自动去除）--startRowNo去除顶部多余的内容
                ignoreLeftOrTop(importVo.getStartRowNo(), rowIterator);
                // TODO 映射表头与entry(单行表头)
                Class<? extends Object> clz = CommonUtil.getClassInstance(importVo.getType());
                Map<Integer, ExcelEntry> headerEntries = mappingHeader2Entry(importVo, rowIterator);
                // 继续处理表中的数据（（使用迭代器和elements能够自动去除）--startCellNo去除左侧多余的内容）
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    ignoreLeftOrTop(importVo.getStartCellNo(), cellIterator);
                    try {
                        Object t = clz.newInstance();
                        readRow2Obj(importVo, headerEntries, cellIterator, t);
                        resultList.add(t);
                    } catch (InstantiationException | IllegalAccessException | OutOfColumnThresholdException e) {
                        e.printStackTrace();// newInstance 异常
                        rowFailCount = increaseRowAndThrowIfPossible(rowFailCount, importVo.getRowFailThreshold());
                    }
                }
            }
        } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    private void readRow2Obj(ExcelOfImportVo importVo, Map<Integer, ExcelEntry> headerEntries, Iterator<Cell> cellIterator, Object t) throws IllegalAccessException, InstantiationException {
        int cellFailCount = 0;
        // 待处理实例集合（key为header），需要 entry 信息，value
        // 待处理指：xml中setter/getter包含了if条件，不能立即判断条件是否成立，因为要判断的条件可能在之后的列才会设置值
        // 出现这种情况，程序的判断依据是：对象值为null，或对象属性值为null，如：caseInfoVo=null 或 caseInfoVo.caseType=null
        Map<String, PendingInfo> pendingMap = new HashMap<>();
        // propertyValued 存储的是 被赋值了的 property
        Set<String> propertyValued = new HashSet<>();
        // 第一次遍历（边读取excel，边设置值）
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            try {
                ExcelEntry excelEntry = headerEntries.get(cell.getColumnIndex());// 表头每一列对应的 entry
                Object value = getValueFromCell(cell, excelEntry);
                checkRequired(excelEntry, cell, value);// 检查是否配置了 必需，与当前 value 是否为 null
                setValue2Property(t, value, excelEntry, pendingMap, propertyValued, null);
            } catch (SecurityException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                // 没找到该方法或设置值失败，就跳过
                cellFailCount = increaseColumnAndThrowIfPossible(cellFailCount, importVo.getColumnFailThreshold());
            }
        }
        // 待处理的entry集合
        if (!pendingMap.isEmpty()) {
            // 处理待处理的 entry 集合
            cellFailCount = processPendingOfSetter(t, pendingMap, propertyValued, cellFailCount);
            // 检查是否所有的待处理 entry 集合都被处理完成
            checkAllPendingProcessed(pendingMap);
        }
    }

    /**
     * 检查 配置是否设置必需，与值是否为 null
     *
     * @param excelEntry
     * @param cell
     * @param value
     */
    private void checkRequired(ExcelEntry excelEntry, Cell cell, Object value) {
        Boolean required = excelEntry.getRequired();
        if (Boolean.TRUE.equals(required) && value == null) {
            // 如果配置了 必需，但是又没有值
            int rowIndex = cell.getRowIndex();
            int columnIndex = cell.getColumnIndex();
            throw new RequireException("当前" +
                    "行（索引：" + rowIndex + "）、" +
                    "列（索引：" + columnIndex + "，header：" + excelEntry.getHeader() + "）" +
                    "没有值（value = null），" +
                    "而此列是必需的（required = " + required + "）");
        }
    }

    /**
     * 处理 待处理 entry 集合（根据if判断来获取其中的getter、setter）<br/>
     * 注意：返回 cellFailCount，用于判断是否需要抛出异常，因为这个方法也是 处理单个 row 的最后一个方法，所以也可以不返回 count
     *
     * @param t
     * @param pendingMap
     * @author huanghao
     * @date 2017年4月24日下午4:53:22
     */
    private int processPendingOfSetter(Object t, Map<String, PendingInfo> pendingMap, Set<String> propertyValued, int cellFailCount) {
        int lastSize;
        do {
            lastSize = pendingMap.size();
            // 进行pendingMap.size 次数的遍历
            for (Iterator<Entry<String, PendingInfo>> entryIterator = pendingMap.entrySet().iterator(); entryIterator.hasNext(); ) {
                Entry<String, PendingInfo> entry = entryIterator.next();
                PendingInfo pendingInfo = entry.getValue();
                try {
                    // 此方法中可能会产生 remove 操作（当）
                    setValue2Property(t, pendingInfo.getValue(), pendingInfo.getEntry(), pendingMap, propertyValued, entryIterator);
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                    cellFailCount = increaseColumnAndThrowIfPossible(cellFailCount, pendingInfo.getEntry().getExcelVo().getColumnFailThreshold());
                }
            }
        } while (lastSize != pendingMap.size());
        // 当 size 相等时，则表示无法通过 setValue2Property 设置属性值，或已没有元素，此时就结束循环
        return cellFailCount;
    }

    /**
     * 检查是否所有的待处理 entry 集合都被处理完成
     *
     * @param pendingMap
     * @author huanghao
     * @date 2017年4月24日下午4:50:33
     */
    private void checkAllPendingProcessed(Map<String, PendingInfo> pendingMap) {
        if (!pendingMap.isEmpty()) {
            StringBuilder msg = new StringBuilder("列（'");
            for (Entry<String, PendingInfo> entry : pendingMap.entrySet()) {
                msg.append(entry.getValue().getEntry().getHeader()).append(",");
            }
            msg.delete(msg.length() - 1, msg.length());
            throw new IllegalArgumentException(
                    msg.append("'）的值无法动态设置到指定的属性中，"
                            + "请确定xml中的条件是否正确，"
                            + "确定条件中的对象是否为 null，"
                            + "确定是否存在循环引用的情况")
                            .toString());
        }
    }

    private int increaseRowAndThrowIfPossible(int rowFailCount, int rowFailThreshold) {
        rowFailCount++;
        if (rowFailCount > rowFailThreshold) {
            throw new OutOfRowThresholdException("当前 sheet，row 设置失败次数超出设定的阈值，设定值 = " + rowFailThreshold + "，实际值 = " + rowFailCount);
        }
        return rowFailCount;
    }

    private int increaseColumnAndThrowIfPossible(int cellFailCount, int ColumnFailThreshold) {
        cellFailCount++;
        if (cellFailCount > ColumnFailThreshold) {
            throw new OutOfColumnThresholdException("当前行，cell 设置失败次数超出设定的阈值，设定值 = " + ColumnFailThreshold + "，实际值 = " + cellFailCount);
        }
        return cellFailCount;
    }

    /**
     * @param t             将值设置到此对象的属性上，并通过 copy 值到指定的其他属性上
     * @param value         单元格的值
     * @param excelEntry    当前列对应的excelEntry
     * @param pendingMap    （key为header）
     *                      待处理entry的Map集合(如果是读取excel时遍历，则是往其中放，如果是处理pendingMap，
     *                      则处理完成后需要将其中的删除)
     * @param entryIterator pendingMap对应的当前的迭代器，可用于删除pendingMap中的元素
     * @author huanghao
     * @date 2017年4月20日下午4:11:19
     */
    private void setValue2Property(Object t, Object value, ExcelEntry excelEntry, Map<String, PendingInfo> pendingMap, Set<String> propertyValued,
                                   Iterator<Entry<String, PendingInfo>> entryIterator)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        if (excelEntry != null && value != null) {
            // 将属性设置到 t 对象中
            String setter = excelEntry.getSetter();
            // 如果 setter 为空，则表示没有直接的 setter，xml 中是包含条件的 setter
            if (CommonUtil.isEmpty(setter)) {
                // 根据 if 判断，从 ELs 中获取最终的 setter
                setter = getSetterFormELs(value, t, excelEntry, pendingMap, propertyValued, entryIterator);
                // 不能注入，因为设置一次，应用到所有的 excelEntry 中去了，
                // 因为 setter 是根据具体的条件来决定的，所以每一行都可能不同，所以不能设置到 excelEntry 中去，必须每一次都去判断 if
                // excelEntry.setSetter(setter);
            }
            if (setter != null) {
                setNestedPropertyForced(t, value, setter);
                propertyValued.add(setter);
            }
            // 处理 copyTo 配置，要不要与 setter 同步，即：setter 不为 null 的时候，才处理 copyTo
            String copyTo = excelEntry.getCopyTo();
            if (CommonUtil.isNotEmpty(copyTo)) {
                String[] copyToArr = copyTo.split(Constants.REGEX_OF_COPY_TO);
                for (String destProperty : copyToArr) {
                    // 进行复制
                    setNestedPropertyForced(t, value, destProperty);
                    propertyValued.add(destProperty);
                }
            }
        }
    }

    /**
     * 强制设置嵌套属性（这里的强制是指：如果嵌套对象没有实例化，则自动将其实例化后，再设置到嵌套对象的属性上）
     *
     * @param t        将嵌套属性设置到此对象上
     * @param value    属性值
     * @param property 属性名
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @author huanghao
     * @date 2017年4月20日下午4:16:47
     */
    private void setNestedPropertyForced(Object t, Object value, String property)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        // 解决属性为对象，对象为null的问题
        CommonUtil.instanceNestPropertyIfNull(t, property);
        PropertyUtils.setNestedProperty(t, property, value);// TODO setDealCount double 不能到 int
    }

    /**
     * 选择性的返回 setter、getter 对应的属性值，原本是带条件的 setter/getter，此方法
     *
     * @param value          setter: 读取 excel 的<strong><i>单元格值<i/></strong>  getter：单元格 <strong><i>Cell 对象<i/></strong>
     * @param obj            setter：要将值设置到的对象  getter：从中获取值的对象
     * @param excelEntry     （如果是第一次处理，则其不存在pendingMap中，如果是处理pendingMap时， 则是从pendingMap中 pendingInfo 的 excelEntry）
     * @param pendingMap     待处理的映射 （key为header）
     * @param propertyValued setter：已设置了值的属性  getter：已获取了值的属性
     * @param entryIterator  迭代器，为了从 pendingMap 中移除元素，注意：<strong>在 processPending 时才需要传入<i> entry 的迭代器</i>，在第一次遍历的时候，传入 <i>null</i></strong>
     * @return 返回有效的 property 值（如果条件满足），如：caseInfo.caseNo
     * @author huanghao
     * @date 2017年4月20日上午10:12:48
     */
    private String getSetterFormELs(Object value, Object obj, ExcelEntry excelEntry, Map<String, PendingInfo> pendingMap,
                                    Set<String> propertyValued, Iterator<Entry<String, PendingInfo>> entryIterator) {
        String setter = null;
        Map<String, String> setterELs = excelEntry.getSetterELs();
        // 可以取消 pendingFlag，当：需要每一次都遍历所有的 if 标签内的 test 条件时（这种需求可能是存在有其他的 test 条件成立，进而就可以直接获取到此 setter）
        boolean pendingFlag = false;// true表示放入了pendingMap中，需要在下一次遍历进行再次处理
        Map<String, Set<String>> dependencies = excelEntry.getDependencies();// 这是当前 excelEntry 的所有依赖属性
        String mappedPendingKey;// 将其作为 pendingMap 的 key
        ExcelVo excelVo = excelEntry.getExcelVo();
        if (Boolean.TRUE.equals(excelVo.getIsIndexWay())) {
            mappedPendingKey = excelEntry.getColumnIndex();
        } else {
            mappedPendingKey = excelEntry.getHeader();
        }
        // 遍历的是 entry 的所有 if 对象，即，条件 与 setter 的键值对
        // for (Iterator<Entry<String, String>> iterator = setterELs.entrySet().iterator(); iterator.hasNext() && !pendingFlag; ) {
        //     Entry<String, String> entry = iterator.next();
        for (Entry<String, String> entry : setterELs.entrySet()) {
            String test = entry.getKey();// 条件：if 的 test 值
            String text = entry.getValue();// 值：if 标签节点的内容
            if (CommonUtil.isEmpty(test) || CommonUtil.isEmpty(text)) {
                continue;
            }
            try {
                // 需要将 entry 放入 pendingMap 中的情况：1.对象为 null，获取其属性判断异常；2.对象的属性为null，因为其可能还没有赋值，但是需要排除这个 cell 本来就没有值
                // 首先判断是否为 null，如果是，则找到此依赖，获取他的值
                // 加入 propertyValued 的判断，用来判断此表头对应的数据是否已被赋值
                // 这里正确的判断应该是：判断 propertyValued 中是否有 dependencies 中不存在的值
                // 判断依据如下：
                // propertyValued 中是否包含当前 test 对应的依赖            pendingMap 中是否已包含了对应的待处理对象
                if (!propertyValued.containsAll(dependencies.get(test))) {
                    if (!pendingMap.containsKey(mappedPendingKey)) {
                        pendingMap.put(mappedPendingKey, new PendingInfo(excelEntry, value));
                    }
                } else if (parseCondition(obj, test)) {
                    setter = text;
                    // 如果是处理待处理映射集合时，则从其中删除已有的
                    if (pendingMap.containsKey(mappedPendingKey)) {
                        if (entryIterator != null) {
                            entryIterator.remove();// 需要使用迭代器来移除元素，否则抛出并发修改异常；
                        } else {
                            pendingMap.remove(mappedPendingKey);
                        }
                    }
                    break;// 只要某个条件为true，则不进行后面的判断
                    // } else {
                    // 还有个情况，如果判断指定属性值本身就是 null，但是此指定的属性值还没有获取到，根据目前的代码，此时条件会判断成功，而实际情况下，不应该通过
                    // 此情况在导入的时候才会出现，因为导入是一步一步的获取数据，而导出的时候，是直接就有所有的数据了，所以不需要配置依赖的关系，也不需要考虑获取顺序的问题
                    // 正确的方式应该是：首先在读取配置的时候，解析配置中的依赖关系，如果存在循环依赖，或没有数据的依赖，则直接抛出异常，
                    // 如果都存在，则在读取excel的时候，按照依赖顺序，依次读取excel的行，（读取是否能够返回上一个cell数据，如果不能，则new一个Map保存表头与cell数据的映射）
                    // 情况一：对象属性值为 null
                    // 进入这里的原因：
                    // 可能是第一次遍历excel，原因可能是：还没获取到表达式需要的属性值（值为 null ），或，没有匹配的条件；
                    // 也有可能是在processPending，原因可能是：判断所有的条件，还没达到条件成立的那一个；
                    // 已通过 dependencies 和 propertyValued 解决
                }
            } catch (OgnlException e) {
                if (e.getMessage().contains("source is null for getProperty")) {// 预知的错误，不打印信息
                    // 情况二：对象值为 null
                    // 只可能在第一次（边读取excel）遍历的时候，才会put，后面的遍历都只是remove
                    if (pendingMap.containsKey(mappedPendingKey)) {
                        pendingMap.put(mappedPendingKey, new PendingInfo(excelEntry, value));
                        // pendingFlag = true;
                    }
                } else {
                    e.printStackTrace();
                    CommonUtil.throwArgument("解析表达式出错，expression=[" + test + "]");
                }
            }
        }
        return setter;
    }

    /**
     * 解析if标签中的test条件(ognl表达式)
     *
     * @param t
     * @param test
     * @return
     * @throws OgnlException
     * @author huanghao
     * @date 2017年4月20日上午10:21:30
     */
    private boolean parseCondition(Object t, String test) throws OgnlException {
        Object value = Ognl.getValue(test, t);
        return Boolean.parseBoolean(value.toString());
    }

    /**
     * 将配置中的header信息映射到entry中
     *
     * @param importVo
     * @param rowIterator
     * @return
     * @author huanghao
     * @date 2017年4月10日上午11:34:20
     */
    private Map<Integer, ExcelEntry> mappingHeader2Entry(ExcelOfImportVo importVo, Iterator<Row> rowIterator) {
        Map<Integer, ExcelEntry> entries = new HashMap<>();
        if (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Map<String, ExcelEntry> excelEntryMap = importVo.getExcelEntryMap();
            for (Iterator<Cell> cellIterator = row.cellIterator(); cellIterator.hasNext(); ) {
                Cell cell = cellIterator.next();
                // 根据表头从configuration中获取setter
                ExcelEntry excelEntry = excelEntryMap.get(cell.getStringCellValue());
                if (excelEntry != null) {
                    // 只获取在xml中配置了entry信息的
                    entries.put(cell.getColumnIndex(), excelEntry);
                }
            }
        }
        return entries;
    }

    /**
     * 忽略顶部或左部多余的row或cell（startRowNo,startCellNo）<br/>
     * 这里的忽略是值指：忽略 excel 有内容的部分（因为使用 迭代器，poi 会自动忽略掉没有内容的行列），如表头信息，excel 左部的作者信息等;
     *
     * @param startNo
     * @param iterator
     * @author huanghao
     * @date 2017年4月7日下午4:44:43
     */
    private void ignoreLeftOrTop(int startNo, Iterator<?> iterator) {
        for (int i = 0; i < startNo && iterator.hasNext(); i++) {
            iterator.next();
        }
    }

    /**
     * 从 cell 单元格中获取内容
     *
     * @param cell
     * @param excelEntry
     * @return
     * @author huanghao
     * @date 2017年4月18日上午9:32:58
     */
    @SuppressWarnings("deprecation")
    private Object getValueFromCell(Cell cell, ExcelEntry excelEntry) {
        CommonUtil.assertArgumentNotNull(cell, "cell不能为null");
        CommonUtil.assertArgumentNotNull(excelEntry, "excelEntry不能为null");
        Object value = null;
        CellType cellTypeEnum = cell.getCellTypeEnum();
        // TODO 这里要考虑 dataformat，否则 NUMERIC 获取到的是 double 类型
        switch (cellTypeEnum) {
            case STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue();
                } else {
                    // TODO poi 对于中文的格式，会判断为 numeric 而不是 date
                    double numericCellValue = cell.getNumericCellValue();
                    String dataFormatOrName = excelEntry.getDataFormat();
                    // FIXME 暂时这么处理，cell 获取的值默认是 double，在设置到 int 类型的时候会报错，同理，byte，short，float 也会报错
                    if ("int".equals(dataFormatOrName) || "0".equals(dataFormatOrName)) {
                        value = (int) numericCellValue;
                    } else {
                        value = numericCellValue;
                    }
                }
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case FORMULA:
                value = cell.getCellFormula();
                break;
            case BLANK:
                // 如果没有设置 blank，默认值就为 null
                if (excelEntry.getBlank() != null) {
                    value = setBlank(excelEntry.getBlank());
                }
                break;
            default:
        }
        // 处理 selectSql
        String dataSource = excelEntry.getDataSource();
        String selectSql = excelEntry.getSelectSql();
        if (CommonUtil.isNotEmpty(selectSql)) {
            value = getValueFromSelectSqlConfig(dataSource, selectSql, value);
        }
        return value;
    }

    /**
     * @param dataSource
     * @param selectSql
     * @param value
     * @return
     * @author huanghao
     * @date 2017年4月25日上午11:50:43
     */
    private Object getValueFromSelectSqlConfig(String dataSource, String selectSql, Object value) {
        if (CommonUtil.isNotEmpty(selectSql)) {
            // 如果是码表信息 -- 连接数据库
            MultiDataSource multiDataSource = null;
            if (CommonUtil.isNotEmpty(dataSource)) {
                // TODO 这里不兼容，提供一个接口，让用户自己实现？如何提供一个默认的实现，还能够与项目的数据源切换方式兼容？
                multiDataSource = engineConfiguration.getMultiDataSource();
                CommonUtil.assertArgumentNotNull(multiDataSource, "没有多数据源切换的实现类，需要设置 multiDataSource");
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("尝试切换到数据源 -> {}", dataSource);
                }
                multiDataSource.switchTo(dataSource);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("切换到数据源 -> {} 成功", dataSource);
                }
            }
            int index = selectSql.indexOf("?");
            if (index != -1 && index == selectSql.lastIndexOf("?")) {
                value = executeSql(value, selectSql);
                if (multiDataSource != null) {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("尝试数据源[{}]的清除", dataSource);
                    }
                    multiDataSource.clear();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("完成数据源[{}]的清除", dataSource);
                    }
                }
            } else {
                CommonUtil.throwArgument("PreparedStatement 必须且只能包含一个参数，参数值即为当前单元格中的值");
            }
        }
        return value;
    }

    /**
     * 执行sql
     *
     * @param value
     * @param selectSql
     * @return
     * @author huanghao
     * @date 2017年4月19日下午2:53:58
     */
    protected Object executeSql(Object value, String selectSql) {
        // TODO 虽然这里目前只有查询，但这里仍然没有被spring管理（由于项目采用的是声明式事务，这里的包没有被事务管理）
        // 如果后期需要执行增删改，也只有手动进行事务的提交回滚
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            DataSource dataSource = this.engineConfiguration.getDataSource();
            connection = dataSource.getConnection();// 只要datasource是连接池的数据源，那这里获取的连接就是连接池中的连接，那么close就是返回到连接池中
            // connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(selectSql);
            preparedStatement.setObject(1, value);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("执行 sql=[{}], params=[{}]", selectSql, value);
            }
            // TODO 目前是查询，是否需要兼容其他执行？应该只需要查询，不需要增删改
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                value = resultSet.getObject(1);
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("执行结束 -> value={}", value);
                }
            }
            // connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 释放连接到连接池中
            CommonUtil.closeAutoCloseable(preparedStatement);
            CommonUtil.closeAutoCloseable(connection);
           /* try {
                if (connection != null) {
                    // connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }*/
        }
        return value;
    }

    private Object setBlank(String valueIfBlank) {
        switch (valueIfBlank) {
            case "null":
                return null;
            case "":// 空串
                return "";
            // case "":// TODO 用于检测并返回日期
            //     return "";
            default:
                return valueIfBlank;
        }
    }

    @Override
    @Deprecated
    // TODO 不应该在 engine 中，因为 excelVo 的实例化是在 parser 中，所以需要在 parser 中进行属性的迁移
    protected void afterCreateExcelVo(ExcelVo excelVo) {
        super.afterCreateExcelVo(excelVo);
        // 属性迁移：失败阈值
        excelVo.setColumnFailThreshold(String.valueOf(engineConfiguration.getGlobalColumnFailThreshold()));
    }

    @Override
    @Deprecated
    protected void afterCreateExcelEntry(ExcelEntry excelEntry) {
        super.afterCreateExcelEntry(excelEntry);
        // 属性迁移：宽度 width
        excelEntry.setWidth(String.valueOf(engineConfiguration.getGlobalMaxColumnWidth()));

    }

    /**
     * @param workbook
     * @author huanghao
     * @date 2017年4月11日下午3:27:42
     */
    private void assertWorkbook(Workbook workbook) {
        CommonUtil.assertArgumentNotNull(workbook, "工作表对象不能为null");
    }

    // ---------------- getter/setter ----------------------
}
