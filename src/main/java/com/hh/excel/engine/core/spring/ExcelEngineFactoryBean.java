package com.hh.excel.engine.core.spring;

import com.hh.excel.engine.config.EngineConfiguration;
import com.hh.excel.engine.config.ExcelConfiguration;
import com.hh.excel.engine.config.parser.ExcelConfigParser;
import com.hh.excel.engine.config.parser.ExcelXmlConfigParser;
import com.hh.excel.engine.core.engine.DefaultExcelEngine;
import com.hh.excel.engine.core.engine.ExcelEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;

/**
 * 与 sqlSessionFactory 关联
 *
 * @author huanghao
 * @date 2017年4月11日下午1:38:28
 */
public class ExcelEngineFactoryBean extends AbstractExcelEngineFactoryBean {
	protected static final String INTERNAL_CONFIG_LOCATION = "com/hh/excel/engine/config/excelMapper.xml";
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelEngineFactoryBean.class);
	protected EngineConfiguration engineConfiguration;

	// protected Resource configLocation;// 存放 excelMapper文件的位置
	// protected DataSource dataSource;
	@Override
	protected void checkProperty() {
		Assert.notNull(this.engineConfiguration.getDataSource(), "Property 'dataSource' is required");
	}

	@Override
	protected ExcelEngine buildExcelEngine() {
		ExcelConfiguration configuration = readConfiguration();
		this.excelEngine = newEngine(engineConfiguration, configuration);
		return this.excelEngine;
	}

	@Override
	protected ExcelEngine newEngine(EngineConfiguration engineConfiguration, ExcelConfiguration configuration) {
		return new DefaultExcelEngine(engineConfiguration, configuration);
	}

	/**
	 * 读取配置文件
	 *
	 * @return 配置对象
	 */
	protected ExcelConfiguration readConfiguration() {
		ExcelConfiguration configuration;
		ExcelConfigParser configParser = new ExcelXmlConfigParser();
		// 读取多个资源文件？同一个容器还是不同容器？读取包下默认的配置文件，并保存到容器中，作为默认配置项；
		Resource[] configLocations = this.engineConfiguration.getConfigLocations();
		if (configLocations != null && configLocations.length > 0) {
			// 读取配置文件
			configParser.addInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(INTERNAL_CONFIG_LOCATION));
			for (Resource resource : configLocations) {
				InputStream inputStream = null;
				try {
					// 添加 各个配置文件 的流 添加到 parser 中
					inputStream = resource.getInputStream();
					configParser.addInputStream(inputStream);
				} catch (IOException e) {
					e.printStackTrace();
					throw new IllegalArgumentException("读取xml失败");
					// } finally {
					// CommonUtil.closeCloseable(inputStream);// 这里关闭后，保存在 inputStream[] 中的数据就没有了
				}
			}
		} else {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("当前没有有效的 configLocations 配置");
			}
		}
		configuration = configParser.config();
		if (configuration == null) {
			throw new IllegalArgumentException("读取xml失败");
		}
		// 属性迁移
		// this.engineConfiguration.setDataSource(this.engineConfiguration.getDataSource());
		return configuration;
	}

	// public void setSpringExcelEngineConfiguration(SpringExcelEngineConfiguration springExcelEngineConfiguration) {
	//     this.engineConfiguration = springExcelEngineConfiguration;
	// }

}

