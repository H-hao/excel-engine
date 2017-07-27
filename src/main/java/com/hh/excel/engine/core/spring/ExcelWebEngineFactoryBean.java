package com.hh.excel.engine.core.spring;

import com.hh.excel.engine.config.EngineConfiguration;
import com.hh.excel.engine.config.ExcelConfiguration;
import com.hh.excel.engine.core.engine.ExcelEngine;
import com.hh.excel.engine.core.engine.DefaultExcelWebEngine;
import com.hh.excel.engine.core.engine.ExcelWebEngine;

public class ExcelWebEngineFactoryBean extends ExcelEngineFactoryBean {

	@Override
	public Class<?> getObjectType() {
		return ExcelWebEngine.class;
	}

	/*@Override
	public ExcelEngine getObject() throws Exception {
		ExcelConfiguration configuration = readConfiguration();
		this.excelEngine = newEngine(engineConfiguration, configuration);
		return this.excelEngine;
	}*/

	@Override
	protected ExcelEngine newEngine(EngineConfiguration engineConfiguration, ExcelConfiguration configuration) {
		return new DefaultExcelWebEngine(engineConfiguration, configuration);
	}

}
