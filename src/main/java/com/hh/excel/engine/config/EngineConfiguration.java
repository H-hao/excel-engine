package com.hh.excel.engine.config;

import com.hh.excel.engine.extend.datasource.MultiDataSource;
import com.hh.excel.engine.extend.template.TemplateManager;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

/**
 * ExcelEngine 对应的配置对象
 * TODO 添加一个 builder 类，用于没有集成spring的时候，进行 configuration 的配置
 * Created by huanghao on 2017/6/29.
 */
public class EngineConfiguration {
	private DataSource dataSource;
	private Resource[] configLocations;// 存放 excelMapper文件的位置
	// -------------------------------- 属性迁移(暂时都不支持，因为还没有一个较好的方案就迁移) --------------------
	/**
	 * 全局 最大 列宽，TODO 待完成
	 */
	@Deprecated
	private int globalMaxColumnWidth = 60 * 256;
	/**
	 * 全局 最大 行高，TODO 待完成
	 */
	@Deprecated
	private int globalMaxRowHeight;
	/**
	 * 全局单个 sheet 失败的阈值，在一个 sheet 中，如果有大于 globalRowFailThreshold 个 row 失败，那么就此 sheet 就算失败<br/>
	 * 默认为 0， 即： 全部正确才算此 sheet 正确
	 */
	@Deprecated
	private int globalRowFailThreshold = 0;
	/**
	 * 全局单个 row 失败的阈值，即：在一个 row 中有大于 globalColumnFailThreshold 个值设置失败后，那么就算此 row 失败<br/>
	 * 默认为 0， 即： 全部正确才算此 sheet 正确
	 */
	@Deprecated
	private int globalColumnFailThreshold = 0;
	// ----------------------- extend -------------------------
	private MultiDataSource multiDataSource;// 切换多数据源的 bean
	private TemplateManager templateManager;// 管理 template 的 bean

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Resource[] getConfigLocations() {
		return configLocations;
	}

	public void setConfigLocations(Resource... configLocations) {
		this.configLocations = configLocations;
	}

	public void setConfigLocation(Resource configLocation) {
		this.configLocations = new Resource[]{configLocation};
	}

	public int getGlobalMaxColumnWidth() {
		return globalMaxColumnWidth;
	}

	@Deprecated
	public void setGlobalMaxColumnWidth(int globalMaxColumnWidth) {
		this.globalMaxColumnWidth = globalMaxColumnWidth;
	}

	public int getGlobalMaxRowHeight() {
		return globalMaxRowHeight;
	}

	@Deprecated
	public void setGlobalMaxRowHeight(int globalMaxRowHeight) {
		this.globalMaxRowHeight = globalMaxRowHeight;
	}

	public int getGlobalRowFailThreshold() {
		return globalRowFailThreshold;
	}

	@Deprecated
	public void setGlobalRowFailThreshold(int globalRowFailThreshold) {
		this.globalRowFailThreshold = globalRowFailThreshold;
	}

	public int getGlobalColumnFailThreshold() {
		return globalColumnFailThreshold;
	}

	@Deprecated
	public void setGlobalColumnFailThreshold(int globalColumnFailThreshold) {
		this.globalColumnFailThreshold = globalColumnFailThreshold;
	}

	public MultiDataSource getMultiDataSource() {
		return multiDataSource;
	}

	public void setMultiDataSource(MultiDataSource multiDataSource) {
		this.multiDataSource = multiDataSource;
	}

	public TemplateManager getTemplateManager() {
		return templateManager;
	}

	public void setTemplateManager(TemplateManager templateManager) {
		this.templateManager = templateManager;
	}
}
