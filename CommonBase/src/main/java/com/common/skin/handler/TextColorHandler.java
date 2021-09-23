package com.common.skin.handler;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

/**
 * 文本颜色处理器
 *
 * @author LiuFeng
 * @data 2021/8/24 18:29
 */
public class TextColorHandler extends ColorStateListHandler {

    @Override
    protected void handle(View view, ColorStateList color) {
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(color);
        }
    }
}
