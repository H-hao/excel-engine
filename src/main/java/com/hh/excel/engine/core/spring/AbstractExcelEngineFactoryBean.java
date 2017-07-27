package com.hh.excel.engine.core.spring;

import com.hh.excel.engine.config.EngineConfiguration;
import com.hh.excel.engine.config.ExcelConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import com.hh.excel.engine.core.engine.ExcelEngine;

public abstract class AbstractExcelEngineFactoryBean implements FactoryBean<ExcelEngine> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExcelEngineFactoryBean.class);
	protected volatile ExcelEngine excelEngine;

	@Override
	public ExcelEngine getObject() throws Exception {
	    if (LOGGER.isInfoEnabled()) {
	        LOGGER.info("准备实例化 ExcelEngine");
	    }
		if (this.excelEngine == null) {
			checkProperty();
			synchronized (this) {
				if (this.excelEngine == null) {
					this.excelEngine = buildExcelEngine();
				}
			}
		}
		if (LOGGER.isInfoEnabled()) {
		    LOGGER.info("实例化 ExcelEngine 结束");
		}
		return this.excelEngine;
	}

	@Override
	public Class<?> getObjectType() {
		return ExcelEngine.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	protected abstract void checkProperty();

	protected abstract ExcelEngine buildExcelEngine();

	// TODO 这个 方法 是否需要？要不要与 build 方法合并？
	protected abstract ExcelEngine newEngine(EngineConfiguration engineConfiguration, ExcelConfiguration configuration);

	// /**
	//  * 属性迁移
	//  */
	// protected void migrateProperty(){}

}
