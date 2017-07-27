package com.hh.excel.engine.config;

import com.hh.excel.engine.config.vo.AbstractBaseConfig;
import com.hh.excel.engine.config.vo.ExcelMap;
import com.hh.excel.engine.config.vo.ExcelVo;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 读取到的配置的对象，目前所有读取到的配置都存放在 baseConfigs 中。<br/>
 * 这个类，原来还保存了一些其他的信息（如datasource等），现在已将它们移到engineConfiguration中。<br/>
 * 目前保留这个类。多个 xml 配置文件中的配置信息统一放置到此类的 baseConfigs 中；<br/>
 * TODO 后期再考虑是否需要取消此类，将AbstractBaseConfig作为基类，如果取消这个类，那么 Map 集合应该保存在哪个类中？<br/>
 *
 * @author huanghao
 * @date 2017年4月6日下午4:17:25
 */
public class ExcelConfiguration {

	//region 将默认 font 和 cellStyle 存入 baseConfigs Map 的 key，对应包下面 excelMapper.xml（此 xml 中配置了默认的 font 和 style）
	/**
	 * 默认字体，存放在 baseConfigs Map 中的 id，
	 */
	public static final String DEFAULT_FONT = "defaultFont";
	/**
	 * 默认表头字体，存放在 baseConfigs Map 中的 id，
	 */
	public static final String DEFAULT_HEADER_FONT = "defaultHeaderFont";
	/**
	 * 默认样式，存放在 baseConfigs Map 中的 id，
	 */
	public static final String DEFAULT_CELL_STYLE = "defaultCellStyle";
	/**
	 * 默认表头样式，存放在 baseConfigs Map 中的 id，
	 */
	public static final String DEFAULT_HEADER_STYLE = "defaultHeaderStyle";
	/**
	 * 默认 integer 样式，存放在 baseConfigs Map 中的 id，
	 */
	public static final String DEFAULT_INTEGER_CELL_STYLE = "defaultIntegerCellStyle";
	/**
	 * 默认 double 样式，存放在 baseConfigs Map 中的 id，
	 */
	public static final String DEFAULT_DOUBLE_CELL_STYLE = "defaultDoubleCellStyle";
	/**
	 * 默认 date 样式，存放在 baseConfigs Map 中的 id，
	 */
	public static final String DEFAULT_DATE_CELL_STYLE = "defaultDateCellStyle";
	/**
	 * 默认 中文 date 样式，存放在 baseConfigs Map 中的 id，
	 */
	public static final String DEFAULT_ZH_DATE_CELL_STYLE = "defaultZhDateCellStyle";
	/**
	 * 默认 datetime 样式，存放在 baseConfigs Map 中的 id，
	 */
	public static final String DEFAULT_DATE_TIME_CELL_STYLE = "defaultDateTimeCellStyle";
	/**
	 * 默认 中文 datetime 样式，存放在 baseConfigs Map 中的 id，
	 */
	public static final String DEFAULT_ZH_DATE_TIME_CELL_STYLE = "defaultZhDateTimeCellStyle";
	/**
	 * 默认 String 样式，存放在 baseConfigs Map 中的 id，
	 */
	public static final String DEFAULT_STRING_CELL_STYLE = "defaultStringCellStyle";
	//endregion，

	// 存储根节点的子元素(key都为id)

	//region 将 baseConfigs 分散的多个 map 集合来管理各个配置，暂时不需要分开来处理
	/**
	 * 保存了 excelMapper.xml 文件解析后的信息（各个标签都放入此 Map 中），以标签的 id 为 key
	 */
	private static final Map<String, AbstractBaseConfig> baseConfigs;

	static {
		baseConfigs = new LinkedHashMap<>();
		//region TODO 这里的默认配置更改为 xml 配置的形式
		// 放入默认配置的 font
		// baseConfigs.put(DEFAULT_FONT, new FontVo(DEFAULT_FONT));
		// baseConfigs.put(DEFAULT_HEADER_FONT, new FontVo(DEFAULT_HEADER_FONT));

		// 放入默认配置的 cellStyle（拥有各种 dataFormat）
		// baseConfigs.put(DEFAULT_CELL_STYLE, new CellStyleVo(DEFAULT_CELL_STYLE, ConfigMapping.DATA_FORMAT_GENERAL, DEFAULT_FONT));
		// baseConfigs.put(DEFAULT_HEADER_STYLE, new CellStyleVo(DEFAULT_HEADER_STYLE, ConfigMapping.DATA_FORMAT_GENERAL, DEFAULT_HEADER_FONT));
		// baseConfigs.put(DEFAULT_INTEGER_CELL_STYLE, new CellStyleVo(DEFAULT_INTEGER_CELL_STYLE, ConfigMapping.DATA_FORMAT_INT, DEFAULT_FONT));
		// baseConfigs.put(DEFAULT_DOUBLE_CELL_STYLE, new CellStyleVo(DEFAULT_DOUBLE_CELL_STYLE, ConfigMapping.DATA_FORMAT_DOUBLE, DEFAULT_FONT));
		// baseConfigs.put(DEFAULT_DATE_CELL_STYLE, new CellStyleVo(DEFAULT_DATE_CELL_STYLE, ConfigMapping.DATA_FORMAT_DATE, DEFAULT_FONT));
		// baseConfigs.put(DEFAULT_ZH_DATE_CELL_STYLE, new CellStyleVo(DEFAULT_ZH_DATE_CELL_STYLE, ConfigMapping.DATA_FORMAT_ZH_DATE, DEFAULT_FONT));
		// baseConfigs.put(DEFAULT_DATE_TIME_CELL_STYLE, new CellStyleVo(DEFAULT_DATE_TIME_CELL_STYLE, ConfigMapping.DATA_FORMAT_DATE_TIME, DEFAULT_FONT));
		// baseConfigs.put(DEFAULT_ZH_DATE_TIME_CELL_STYLE, new CellStyleVo(DEFAULT_ZH_DATE_TIME_CELL_STYLE, ConfigMapping.DATA_FORMAT_ZH_DATE_TIME, DEFAULT_FONT));
		// baseConfigs.put(DEFAULT_STRING_CELL_STYLE, new CellStyleVo(DEFAULT_STRING_CELL_STYLE, ConfigMapping.DATA_FORMAT_STRING, DEFAULT_FONT));
		//endregion
	}

	/**
	 * 保存 xml 中 导入，导出 标签的信息
	 */
	@Deprecated
	protected Map<String, ExcelVo> excelVos;
	/**
	 * 保存 xml 中 excelMap 标签中的信息
	 */
	@Deprecated
	protected Map<String, ExcelMap> excelMap;
	//endregion
	/**
	 * 保存 xml 中 font 标签的信息
	 */
	@Deprecated
	protected Map<String, Font> fonts;
	/**
	 * 保存 xml 中 cellStyle 标签的信息
	 */
	@Deprecated
	protected Map<String, CellStyle> cellStyles;

	public ExcelConfiguration() {
		// 进行一些初始化配置
		// excelVos = new HashMap<>();
		// excelMap = new HashMap<>();
		// fonts = new HashMap<>();
		// cellStyles = new HashMap<>();
	}

	public Map<String, AbstractBaseConfig> getBaseConfigs() {
		return baseConfigs;
	}

}
