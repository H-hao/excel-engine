package com.hh.excel.engine.extend.template;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 普通的文件系统的实现
 * Created by huanghao on 2017/7/10.
 */
public class DefaultTemplateManager implements TemplateManager {
    @Override
    public InputStream getTemplateInputStream(String templateLocation) throws FileNotFoundException {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(templateLocation);
    }
}
