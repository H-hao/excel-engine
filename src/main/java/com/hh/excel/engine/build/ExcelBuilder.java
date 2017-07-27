package com.hh.excel.engine.build;

import com.hh.excel.engine.config.ExcelConfiguration;
import com.hh.excel.engine.config.vo.ExcelVo;

/**
 * excelVo 的 build 类<br>
 * 直接通过 ExcelConfigParser.config() 方法读取配置文件
 * 
 * @author huanghao
 * @date 2017年4月6日上午11:26:23
 */
@Deprecated
public abstract class ExcelBuilder {

	/**
	 * 读取所有的xml信息
	 * 
	 * @return
	 * @author huanghao
	 * @date 2017年4月6日下午3:33:52
	 */
	public abstract ExcelVo build(ExcelConfiguration configuration);

}
