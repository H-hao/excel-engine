package com.hh.excel.engine.core.engine;

import com.hh.excel.engine.config.EngineConfiguration;
import com.hh.excel.engine.config.ExcelConfiguration;
import com.hh.excel.engine.config.vo.ExcelOfExportVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class DefaultExcelWebEngine extends DefaultExcelEngine implements ExcelWebEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExcelWebEngine.class);
    protected static final String APPLICATION_OCTET_STREEM = "application/octet-streem";
    protected static final String APPLICATION_MSEXCEL = "application/msexcel";

    public DefaultExcelWebEngine(EngineConfiguration engineConfiguration, ExcelConfiguration configuration) {
        super(engineConfiguration, configuration);
    }

    @Override
    public void exportExcel(HttpServletRequest request, HttpServletResponse response, String mapperId, List<List<?>> data) {
        ServletOutputStream servletOutputStream = null;
        ExcelOfExportVo exportVo = (ExcelOfExportVo) baseConfigs.get(mapperId);
        try {
            String suffix = "." + exportVo.getExportType();
            servletOutputStream = response.getOutputStream();
            response.reset();
            String fileName = processFileName(request, exportVo.getFileName() + suffix);
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            // response.setContentType(APPLICATION_OCTET_STREEM);
            response.setContentType(APPLICATION_MSEXCEL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("导出 Excel([{}]) 到 Web -> 设置请求头完成，准备导出数据(sheet.size=[{}])到响应流", mapperId, data.size());
        }
        super.exportExcel(servletOutputStream, mapperId, data);
    }

    /**
     * 应该不需要这个方法，就算是 web 的文件上传，也可以直接获取 inputStream 并使用 DefaultExcelEngine 的 import 方法来实现上传
     * @param request
     * @param response
     * @param mapperId
     * @return
     */
    @Override
    @Deprecated
    public List<List<Object>> importExcel(HttpServletRequest request, HttpServletResponse response, String mapperId) {
        return null;
    }

    /**
     * 解决导出文件名称时的乱码问题
     *
     * @param request
     * @param fileNames
     * @return
     * @author huanghao
     * @date 2017年3月14日下午7:31:42
     */
    private static String processFileName(HttpServletRequest request, String fileNames) {
        String codedFileName = null;
        String agent = request.getHeader("USER-AGENT");
        try {
            if (null != agent && agent.contains("MSIE") || null != agent && agent.contains("Trident")) {// ie
                codedFileName = URLEncoder.encode(fileNames, "UTF8");
            } else if (null != agent && agent.contains("Mozilla")) {// 火狐,chrome等
                codedFileName = new String(fileNames.getBytes("UTF-8"), "iso-8859-1");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return codedFileName;
    }

}
