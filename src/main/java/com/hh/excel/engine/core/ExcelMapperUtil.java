package com.hh.excel.engine.core;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hh.excel.engine.core.engine.ExcelEngine;

/**
 * 通过mapper配置来进行excel的导入与导出
 * 
 * 不予spring关联，可直接使用的
 * 
 * @author huanghao
 * @date 2017年4月5日上午9:24:50
 */
@Component
@Deprecated
public class ExcelMapperUtil {

	// private static ExcelEngine excelEngine;
	@Autowired
	private ExcelEngine excelEngine;

//	static {
	// // TODO sqlSessionFactory 的问题（如果不与spring关联，怎么获取sqlSessionFactory）
//		excelEngine = new DefaultExcelWebEngine(new ExcelXmlConfigParser().config());
//	}

//	public static List<Object> importExcel(InputStream inputStream, String mapperId) {
	public List<Object> importExcel(InputStream inputStream, String mapperId) {
		List<Object> objs = excelEngine.importExcel(inputStream, mapperId);
		return objs;
	}

}
