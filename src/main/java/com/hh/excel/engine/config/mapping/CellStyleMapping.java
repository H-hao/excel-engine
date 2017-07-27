package com.hh.excel.engine.config.mapping;

import com.hh.excel.engine.config.vo.CellStyleVo;
import org.apache.poi.ss.usermodel.*;

/**
 * 包含一些静态方法，用于 cellStyleVo 中的 string 类型参数 转换为 short/enum 类型字段
 * Created by huanghao on 2017/7/7.
 */
public class CellStyleMapping {

    public static CellStyle transCellStyleVo2CellStyle(CellStyleVo cellStyleVo, CellStyle cellStyle){
        // TODO set 设置属性
        return cellStyle;
    }

    public static VerticalAlignment transVerticalAlignment(String verticalAlignment) {
        switch (verticalAlignment) {
            case "top":
                return VerticalAlignment.TOP;
            case "bottom":
                return VerticalAlignment.BOTTOM;
            case "justify":// 两端对齐
                return VerticalAlignment.JUSTIFY;
            case "distributed":// 分散对齐
                return VerticalAlignment.DISTRIBUTED;
            default:
                return VerticalAlignment.CENTER;
        }
    }

    public static HorizontalAlignment transAlignment(String alignment) {
        switch (alignment) {
            case "center":
                return HorizontalAlignment.CENTER;
            case "center_selection":
                return HorizontalAlignment.CENTER_SELECTION;
            case "distributed":
                return HorizontalAlignment.DISTRIBUTED;
            case "fill":
                return HorizontalAlignment.FILL;
            case "justify":
                return HorizontalAlignment.JUSTIFY;
            case "right":
                return HorizontalAlignment.RIGHT;
            default:
                return HorizontalAlignment.GENERAL;
        }
    }

    public static FillPatternType transFillPattern(String fillPattern) {
        switch (fillPattern) {
            case "alt_bars":
                return FillPatternType.ALT_BARS;
            case "big_spots":
                return FillPatternType.BIG_SPOTS;
            case "bricks":
                return FillPatternType.BRICKS;
            case "diamonds":
                return FillPatternType.DIAMONDS;
            case "fine_dots":
                return FillPatternType.FINE_DOTS;
            case "least_dots":
                return FillPatternType.LEAST_DOTS;
            case "less_dots":
                return FillPatternType.LESS_DOTS;
            case "solid_foreground":
                return FillPatternType.SOLID_FOREGROUND;
            case "sparse_dots":
                return FillPatternType.SPARSE_DOTS;
            case "squares":
                return FillPatternType.SQUARES;
            case "thick_backward_diag":
                return FillPatternType.THICK_BACKWARD_DIAG;
            case "thick_forward_diag":
                return FillPatternType.THICK_FORWARD_DIAG;
            case "thick_horz_bands":
                return FillPatternType.THICK_HORZ_BANDS;
            case "thick_vert_bands":
                return FillPatternType.THICK_VERT_BANDS;
            case "thin_backward_diag":
                return FillPatternType.THIN_BACKWARD_DIAG;
            case "thin_forward_diag":
                return FillPatternType.THIN_FORWARD_DIAG;
            case "thin_horz_bands":
                return FillPatternType.THIN_HORZ_BANDS;
            case "thin_vert_bands":
                return FillPatternType.THIN_VERT_BANDS;
            default:
                return FillPatternType.NO_FILL;
        }
    }

    public static BorderStyle transBorderStyle(String borderStyle) {
        switch (borderStyle) {
            case "dash_dot":
                return BorderStyle.DASH_DOT;
            case "dash_dot_dot":
                return BorderStyle.DASH_DOT_DOT;
            case "dashed":
                return BorderStyle.DASHED;
            case "dotted":
                return BorderStyle.DOTTED;
            case "double":
                return BorderStyle.DOUBLE;
            case "hair":
                return BorderStyle.HAIR;
            case "medium":
                return BorderStyle.MEDIUM;
            case "medium_dash_dot":
                return BorderStyle.MEDIUM_DASH_DOT;
            case "medium_dash_dot_dot":
                return BorderStyle.MEDIUM_DASH_DOT_DOT;
            case "medium_dashed":
                return BorderStyle.MEDIUM_DASHED;
            case "slanted_dash_dot":
                return BorderStyle.SLANTED_DASH_DOT;
            case "thick":
                return BorderStyle.THICK;
            case "thin":
                return BorderStyle.THIN;
            default:
                return BorderStyle.NONE;
        }
    }

}
