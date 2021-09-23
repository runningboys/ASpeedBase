package com.common.skin.handler;

import android.view.View;

/**
 * 透明度处理器
 *
 * @author LiuFeng
 * @data 2021/8/24 18:28
 */
public class AlphaHandler extends FloatHandler {

    @Override
    protected void handle(View view, float value) {
        view.setAlpha(value);
    }
}
