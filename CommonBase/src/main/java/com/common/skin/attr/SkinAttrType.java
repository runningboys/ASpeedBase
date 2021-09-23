package com.common.skin.attr;


import java.util.HashMap;
import java.util.Map;

/**
 * 匹配属性的类型枚举
 *
 * @author LiuFeng
 * @data 2021/8/24 18:15
 */
public enum SkinAttrType {
    SRC("src"),
    BACKGROUND("background"),
    TEXT_COLOR("textColor"),
    HINT_COLOR("hintColor"),
    SECOND_TEXT_COLOR("secondTextColor"),
    BORDER("border"),
    TOP_SEPARATOR("topSeparator"),
    BOTTOM_SEPARATOR("bottomSeparator"),
    RIGHT_SEPARATOR("rightSeparator"),
    LEFT_SEPARATOR("LeftSeparator"),
    ALPHA("alpha"),
    TINT_COLOR("tintColor"),
    BG_TINT_COLOR("bgTintColor"),
    PROGRESS_COLOR("progressColor"),
    TEXT_COMPOUND_TINT_COLOR("tcTintColor"),
    TEXT_COMPOUND_LEFT_SRC("tclSrc"),
    TEXT_COMPOUND_RIGHT_SRC("tcrSrc"),
    TEXT_COMPOUND_TOP_SRC("tctSrc"),
    TEXT_COMPOUND_BOTTOM_SRC("tcbSrc"),
    UNDERLINE("underline"),
    MORE_TEXT_COLOR("moreTextColor"),
    MORE_BG_COLOR("moreBgColor");

    public String type;

    SkinAttrType(String value) {
        this.type = value;
    }


    private static final Map<String, SkinAttrType> typeMap = new HashMap<>();

    static {
        for (SkinAttrType attrType : values()) {
            typeMap.put(attrType.type, attrType);
        }
    }

    /**
     * 解析为枚举类型
     *
     * @param type
     * @return
     */
    public static SkinAttrType parse(String type) {
        return typeMap.get(type);
    }
}
