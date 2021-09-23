package com.common.skin.handler;

import android.content.res.ColorStateList;
import android.view.View;

import com.common.skin.api.SkinHelper;
import com.common.skin.attr.SkinAttr;

/**
 * 颜色状态处理器
 *
 * @author LiuFeng
 * @data 2021/8/24 18:29
 */
public abstract class ColorStateListHandler implements SkinHandler {

    @Override
    public final void handle(View view, SkinAttr skinAttr) {
        int resId = skinAttr.attrValueResId;
        ColorStateList color = SkinHelper.getResourceManager().getColorStateList(resId);
        if (color != null) {
            handle(view, color);
        }
    }

    protected abstract void handle(View view, ColorStateList color);
}
