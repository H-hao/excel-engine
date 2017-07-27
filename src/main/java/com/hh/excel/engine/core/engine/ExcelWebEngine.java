package com.hh.excel.engine.core.engine;

import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ExcelWebEngine extends ExcelEngine {

	void exportExcel(HttpServletRequest request, HttpServletResponse response, String mapperId, List<? extends Object> data);

	List<Object> importExcel(HttpServletRequest request, HttpServletResponse response, String mapperId);
}
