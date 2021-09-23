package com.common.skin.handler;

import android.view.View;

import com.common.skin.attr.SkinAttr;

/**
 * 属性单精度值处理器
 *
 * @author LiuFeng
 * @data 2021/8/24 18:29
 */
public abstract class FloatHandler implements SkinHandler {

    @Override
    public void handle(View view, SkinAttr skinAttr) {
        float value = skinAttr.attrValueData;
        if (value != 0 && value != -1) {
            handle(view, value);
        }
    }

    protected abstract void handle(View view, float value);
}
