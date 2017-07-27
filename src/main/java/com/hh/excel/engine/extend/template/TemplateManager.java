package com.hh.excel.engine.extend.template;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 用于管理 excel 的 template 文件
 * Created by huanghao on 2017/7/10.
 */
public interface TemplateManager {
    InputStream getTemplateInputStream(String templateLocation) throws FileNotFoundException;
}
