package com.hh.excel.engine.config.vo;

import java.util.Map;
import java.util.Set;

/**
 * 封装xml配置中excelImport的信息
 *
 * @param
 * @author huanghao
 * @date 2017年4月6日上午11:05:14
 */
public class ExcelOfImportVo extends ExcelVo {
    /**
     * 这是解析之后的属性，是：entry 之间的依赖关系<br/>
     * 以 header 的名称为 key，<br/>
     * value : key 为 excelEntry 中 if 的 test 内容，value 为 此 test 中包含依赖的 Set 集合<br/>
     */
    private Map<String, Map<String, Set<String>>> dependencies;

    public Map<String, Map<String, Set<String>>> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Map<String, Map<String, Set<String>>> dependencies) {
        this.dependencies = dependencies;
    }

}
