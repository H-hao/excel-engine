package com.hh.excel.engine.core.engine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ExcelWebEngine extends ExcelEngine {

    void exportExcel(HttpServletRequest request, HttpServletResponse response, String mapperId, List<List<?>> data);

    List<List<Object>> importExcel(HttpServletRequest request, HttpServletResponse response, String mapperId);
}
