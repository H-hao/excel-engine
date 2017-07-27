package com.hh.excel.engine.config.parser;

import com.hh.excel.engine.config.ExcelConfiguration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 将其他配置（指：xml配置）封装为配置对象
 *
 * @author huanghao
 * @date 2017年4月6日上午11:26:59
 */
public abstract class ExcelConfigParser {
	protected static final String DEFAULT_MAPPER_FILE_NAME = "excelMapper";// 默认配置文件名
	// 各个节点名称
	protected static final String ELEMENT_EXCEL_EXPORT = "excelExport";
	protected static final String ELEMENT_EXCEL_IMPORT = "excelImport";
	protected static final String ELEMENT_EXCEL_MAP = "excelMap";
	protected static final String ELEMENT_STYLE = "style";
	protected static final String ELEMENT_FONT = "font";

	/**
	 * 所有的 配置信息 都汇总到此字段中
	 */
	protected static volatile ExcelConfiguration configuration;

	protected List<InputStream> inputStreams = new ArrayList<>();

	public ExcelConfiguration config() {
		if (configuration == null) {
			synchronized (this) {
				if (configuration == null) {
					configuration = new ExcelConfiguration();
				}
			}
		}
		doConfig();
		return configuration;
	}

	/**
	 * 根据 配置文件 将配置信息写入到 configuration 字段中
	 *
	 * @return
	 */
	protected abstract void doConfig();


	//region 添加 流 的 API( add 方法)
	public void addInputStream(InputStream inputStream) {
		this.inputStreams.add(inputStream);
	}

	public void addInputStream(InputStream... inputStreams) {
		this.inputStreams.addAll(Arrays.asList(inputStreams));
	}

	public void addInputStream(Collection<InputStream> inputStreams) {
		this.inputStreams.addAll(inputStreams);
	}
	//endregion

}
