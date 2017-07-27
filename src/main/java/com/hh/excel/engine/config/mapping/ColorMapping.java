package com.hh.excel.engine.config.mapping;

import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * 包含一些静态方法，用于公用的 string 类型参数 转换为 short/enum 类型字段
 * Created by huanghao on 2017/7/7.
 */
public class ColorMapping {

    public static short transIndexedColorsToShort(String color) {
        short toShortColor;
        // TODO 增加对自定义 color 的判断
        try {
            toShortColor = Short.parseShort(color);
        } catch (NumberFormatException e) {
            toShortColor = transIndexedColors(color).getIndex();
        }
        return toShortColor;
    }

    public static IndexedColors transIndexedColors(String color) {
        switch (color) {
            case "black":
                return IndexedColors.BLACK;
            case "white":
                return IndexedColors.WHITE;
            case "red":
                return IndexedColors.RED;
            case "bright_green":
                return IndexedColors.BRIGHT_GREEN;
            case "blue":
                return IndexedColors.BLUE;
            case "yellow":
                return IndexedColors.YELLOW;
            case "pink":
                return IndexedColors.PINK;
            case "turquoise":
                return IndexedColors.TURQUOISE;
            case "dark_red":
                return IndexedColors.DARK_RED;
            case "green":
                return IndexedColors.GREEN;
            case "dark_blue":
                return IndexedColors.DARK_BLUE;
            case "dark_yellow":
                return IndexedColors.DARK_YELLOW;
            case "violet":
                return IndexedColors.VIOLET;
            case "teal":
                return IndexedColors.TEAL;
            case "grey_25_percent":
                return IndexedColors.GREY_25_PERCENT;
            case "grey_50_percent":
                return IndexedColors.GREY_50_PERCENT;
            case "cornflower_blue":
                return IndexedColors.CORNFLOWER_BLUE;
            case "maroon":
                return IndexedColors.MAROON;
            case "lemon_chiffon":
                return IndexedColors.LEMON_CHIFFON;
            case "orchid":
                return IndexedColors.ORCHID;
            case "coral":
                return IndexedColors.CORAL;
            case "royal_blue":
                return IndexedColors.ROYAL_BLUE;
            case "light_cornflower_blue":
                return IndexedColors.LIGHT_CORNFLOWER_BLUE;
            case "sky_blue":
                return IndexedColors.SKY_BLUE;
            case "light_turquoise":
                return IndexedColors.LIGHT_TURQUOISE;
            case "light_green":
                return IndexedColors.LIGHT_GREEN;
            case "light_yellow":
                return IndexedColors.LIGHT_YELLOW;
            case "pale_blue":
                return IndexedColors.PALE_BLUE;
            case "rose":
                return IndexedColors.ROSE;
            case "lavender":
                return IndexedColors.LAVENDER;
            case "tan":
                return IndexedColors.TAN;
            case "light_blue":
                return IndexedColors.LIGHT_BLUE;
            case "aqua":
                return IndexedColors.AQUA;
            case "lime":
                return IndexedColors.LIME;
            case "gold":
                return IndexedColors.GOLD;
            case "light_orange":
                return IndexedColors.LIGHT_ORANGE;
            case "orange":
                return IndexedColors.ORANGE;
            case "blue_grey":
                return IndexedColors.BLUE_GREY;
            case "grey_40_percent":
                return IndexedColors.GREY_40_PERCENT;
            case "dark_teal":
                return IndexedColors.DARK_TEAL;
            case "sea_green":
                return IndexedColors.SEA_GREEN;
            case "dark_green":
                return IndexedColors.DARK_GREEN;
            case "olive_green":
                return IndexedColors.OLIVE_GREEN;
            case "brown":
                return IndexedColors.BROWN;
            case "plum":
                return IndexedColors.PLUM;
            case "indigo":
                return IndexedColors.INDIGO;
            case "grey_80_percent":
                return IndexedColors.GREY_80_PERCENT;
            default:
                return IndexedColors.AUTOMATIC;
        }
    }
}
