package com.hh.excel.engine.common;

/**
 * 核心帮助类，都是与 Excel 相关的一些方法，如在：config 和 engine 中使用的方法；
 * Created by huanghao on 2017/7/10.
 */
public class CoreUtil {

    /**
     * 从 excel 的定位字符串中获取当前列的 int 值
     *
     * @param excelIndex excel 的定位字符串值，如：CA18
     * @return 定位字符串中表示当前列的信息的 int 值，如：E19 -> 返回 4
     */
    public static int getColumnIndexFormExcelLocation(String excelIndex) {
        CommonUtil.assertArgumentNotEmpty(excelIndex, "excel 定位字符串值不能为 null 或 空串");
        // 简单的正则检查 -> 这里的位数是否需要限制，如果以后 excel 支持更多的单元格，那么这里就不添加限制更好
        CommonUtil.assertTrue(excelIndex.matches("[A-Za-z]+\\d*"), "excel 定位字符串值不合法，请在 excel 中确认");
        String[] columnStr = excelIndex.split("[^A-Za-z]+");
        // 全部转换为 大写
        String index = columnStr[0].toUpperCase();
        CommonUtil.assertTrue(index.matches("^[A-Z]+$"), "excel 定位字符串值不合法，请在 excel 中确认");
        int parsedIndex = 0;
        // 将 index 字母转换为 数字
        for (int i = 0, len = index.length() - 1; i <= len; i++) {
            char ch = index.charAt(i);
            parsedIndex += (ch - 65 + 1) * (Math.pow(26, len - i));
        }
        return parsedIndex - 1;
    }

    public static int getRowIndexFormExcelLocation(String excelIndex) {
        CommonUtil.assertArgumentNotEmpty(excelIndex, "excel 定位字符串值不能为 null 或 空串");
        // 简单的正则检查 -> 这里的位数是否需要限制，如果以后 excel 支持更多的单元格，那么这里就不添加限制更好
        CommonUtil.assertTrue(excelIndex.matches("[A-Za-z]*\\d+"), "excel 定位字符串值不合法，请在 excel 中确认");
        String[] columnStr = excelIndex.split("^[a-zA-Z]*");
        String index = columnStr[1];
        CommonUtil.assertTrue(index.matches("^\\d+$"), "excel 定位字符串值不合法，请在 excel 中确认");
        return Integer.parseInt(index) - 1;
    }

}
