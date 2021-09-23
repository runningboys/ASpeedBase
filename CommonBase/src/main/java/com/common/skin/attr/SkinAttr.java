package com.common.skin.attr;


/**
 * 皮肤的属性
 *
 * @author LiuFeng
 * @data 2021/8/24 18:14
 */
public class SkinAttr {
    /**
     * 属性类型, 例如: background、textSize、textColor
     */
    public SkinAttrType attrType;

    /**
     * 资源名称, 例如：skin_common_background、skin_app_window_bg_color
     */
    public String attrValueName;

    /**
     * 资源类型, 例如：color、drawable、attr
     */
    public String attrValueTypeName;

    /**
     * 资源id，例如：2130745655
     */
    public int attrValueResId;

    /**
     * float值数据，例如：alpha(0.6f)
     */
    public float attrValueData;


    public SkinAttr(SkinAttrType attrType, String attrValueName) {
        this.attrType = attrType;
        this.attrValueName = attrValueName;
    }
}
