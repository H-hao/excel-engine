package com.hh.excel.engine;

import com.hh.excel.engine.config.EngineConfiguration;
import com.hh.excel.engine.config.ExcelConfiguration;
import com.hh.excel.engine.core.engine.DefaultExcelEngine;
import com.hh.excel.engine.core.engine.ExcelEngine;
import org.junit.Before;
import org.junit.Test;

/**
 * 测试 导入导出
 * Created by 黄浩 on 2017/7/30 0030.
 */
public class ExcelEngineTest {
	@Before
	public void setUp() throws Exception {
		ExcelConfiguration excelConfiguration = new ExcelConfiguration();
		EngineConfiguration engineConfiguration = new EngineConfiguration();
		ExcelEngine excelEngine = new DefaultExcelEngine(engineConfiguration, excelConfiguration);
	}

	@Test
	public void testImport(){

	}

	@Test
	public void testExport(){

	}

	@Test
	public void testImportByWeb(){

	}

	@Test
	public void testExportByWeb(){

	}
}
