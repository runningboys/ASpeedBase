package com.common.skin.attr;

import android.text.TextUtils;
import android.util.AttributeSet;

import com.common.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 处理支持的皮肤属性
 *
 * @author LiuFeng
 * @data 2021/8/24 18:18
 */
public class SkinAttrSupport {
    private static final String ATTR_PREFIX = "skin";

    /**
     * 通过资源截取
     *
     * @param attrs
     * @return
     */
    public static List<SkinAttr> getSkinAttrs(AttributeSet attrs) {
        List<SkinAttr> skinAttrs = new ArrayList<>();
        //在这里循环遍历出这个activity中所包含的所有资源
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String attrName = attrs.getAttributeName(i);//属性名
            String attrValue = attrs.getAttributeValue(i);//属性值

            //通过属性名获得到属性类型
            SkinAttrType attrType = SkinAttrType.parse(attrName);
            if (attrType != null && (attrValue.startsWith("@") || attrValue.startsWith("?"))) {
                //通过属性值获得属性id，以@开头验证
                int resId = Integer.parseInt(attrValue.substring(1));
                //通过属性id获得这个属性的名称，例如R文件id是0x7f050000；可以通过这个id得到它的命名：例如：skin_index_bg
                String entryName = getSkinEntryName(resId);
                if (entryName != null) {
                    //我们验证skin以后的资源名称加上带有资源类型的枚举，例如：color类型的枚举
                    SkinAttr attr = new SkinAttr(attrType, entryName);
                    skinAttrs.add(attr);
                }
            }
        }

        return skinAttrs;
    }


    /**
     * tag的样式，我们可以根据tag来截取
     *
     * @param tagStr 样式：src:skin_left_menu_icon|textColor:skin_color_red|
     * @return
     */
    public static List<SkinAttr> getSkinTags(String tagStr) {
        List<SkinAttr> skinAttrs = new ArrayList<>();

        // 为空，则表示它不是需要换肤的
        if (TextUtils.isEmpty(tagStr)) {
            return skinAttrs;
        }

        //将string截取成一|分隔符的字符串数组
        String[] items = tagStr.split("\\|");
        for (String item : items) {
            //截取出资源名和资源类型
            String[] resItems = item.split(":");
            String resType = resItems[0];
            String resName = resItems[1];

            //通过属性名获得到属性类型
            SkinAttrType attrType = SkinAttrType.parse(resType);
            if (attrType != null) {
                SkinAttr attr = new SkinAttr(attrType, resName);
                skinAttrs.add(attr);
            }
        }

        return skinAttrs;
    }

    /**
     * 通过资源id，获取皮肤的资源名称
     *
     * @param resId
     * @return
     */
    public static String getSkinEntryName(int resId) {
        if (resId == 0 || resId == -1) {
            return null;
        }

        String entryName = AppUtil.getResources().getResourceEntryName(resId);
        if (entryName.startsWith(ATTR_PREFIX)) {
            return entryName;
        }

        return null;
    }
}