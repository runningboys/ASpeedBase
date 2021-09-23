package com.common.skin.handler;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

/**
 * 提示色处理器
 *
 * @author LiuFeng
 * @data 2021/8/24 18:29
 */
public class HintColorHandler extends ColorStateListHandler {

    @Override
    protected void handle(View view, ColorStateList color) {
        if (view instanceof TextView) {
            ((TextView) view).setHintTextColor(color);
            return;
        }

        if (view instanceof TextInputLayout) {
            ((TextInputLayout) view).setHintTextColor(color);
        }
    }
}
