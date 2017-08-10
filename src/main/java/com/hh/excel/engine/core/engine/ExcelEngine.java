package com.hh.excel.engine.core.engine;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 对外开发的接口
 *
 * @author huanghao
 * @date 2017年4月6日下午2:11:33
 */
public interface ExcelEngine {

    /**
     * 将指定的 data 数据导出到 outputStream 流中<br/>
     * 对于多 sheet，data 类型应该是：List&lt;List&lt;Object&gt;&gt;，Object 即是一个实体对象
     *
     * @param outputStream
     * @param mapperId
     * @param data
     */
    void exportExcel(OutputStream outputStream, String mapperId, List<List<?>> data);

    /**
     * 导入 excel 数据 为 list &lt;Object&gt; 集合
     * 对于多 sheet，data 类型应该是：List&lt;List&lt;Object&gt;&gt;，Object 即是一个实体对象
     * <p>
     * 注意：此方法可能抛出 RequireException 异常（当 entry 配置了 required，但是 value = null 时抛出）
     * </p>
     *
     * @param inputStream
     * @param mapperId
     * @return
     */
    List<List<Object>> importExcel(InputStream inputStream, String mapperId);

}
