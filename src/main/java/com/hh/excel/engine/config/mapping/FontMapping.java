package com.hh.excel.engine.config.mapping;

import com.hh.excel.engine.config.vo.FontVo;
import org.apache.poi.ss.usermodel.Font;

/**
 * 包含一些静态方法，用于 fontVo 中的 string 类型参数 转换为 short/enum 类型字段
 * Created by huanghao on 2017/7/7.
 */
public class FontMapping {

    public static Font transFontVo2Font(FontVo fontVo, Font font){
        // TODO set 设置属性
        return font;
    }

    public static byte transCharSet(String charSet) {
        switch (charSet) {
            case "ansi":
                return Font.ANSI_CHARSET;
            case "symbol":
                return Font.SYMBOL_CHARSET;
            default:
                return Font.DEFAULT_CHARSET;
        }
    }

    public static byte transUnderline(String underline) {
        switch (underline) {
            case "single":
                return Font.U_SINGLE;
            case "double":
                return Font.U_DOUBLE;
            case "singleAcount":
                return Font.U_SINGLE_ACCOUNTING;
            case "doubleAcount":
                return Font.U_DOUBLE_ACCOUNTING;
            default:
                return Font.U_NONE;
        }
    }

    public static short transTypeOffset(String typeOffset) {
        switch (typeOffset) {
            case "sub":
                return Font.SS_SUB;
            case "super":
                return Font.SS_SUPER;
            default:
                return Font.SS_NONE;
        }
    }
}
