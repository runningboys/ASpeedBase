package com.common.skin.handler;

import android.content.res.ColorStateList;
import android.view.View;

/**
 * 颜色处理器
 *
 * @author LiuFeng
 * @data 2021/8/24 18:29
 */
public abstract class ColorHandler extends ColorStateListHandler {

    @Override
    protected void handle(View view, ColorStateList color) {
        handle(view, color.getDefaultColor());
    }

    protected abstract void handle(View view, int color);
}
