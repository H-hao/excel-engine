package com.hh.excel.engine.config.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * 在xml中配置的映射关系 (excelMap( &lt;result property="caseNo"
 * display="案件代码">&lt;/result>)))
 * 
 * @author huanghao
 * @date 2017年4月6日上午10:54:00
 */
public class ExcelMap extends AbstractBaseConfig {
	/**
	 * typeName : 映射的java类型
	 */
	private String type;
	/**
	 * mapper : 详细的映射关系
	 */
	private Map<String, String> mapper = new HashMap<>();

	public ExcelMap() {}

	public ExcelMap(String id) {
		this();
		this.setId(id);
	}

	public ExcelMap(String id, String type) {
		this(id);
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, String> getMapper() {
		return mapper;
	}

	public void setMapper(Map<String, String> mapper) {
		this.mapper = mapper;
	}

}
