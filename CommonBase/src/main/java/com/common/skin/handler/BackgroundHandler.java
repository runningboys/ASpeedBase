package com.common.skin.handler;

import android.view.View;

/**
 * 背景处理器
 *
 * @author LiuFeng
 * @data 2021/8/24 18:28
 */
public class BackgroundHandler extends ColorHandler {

    @Override
    protected void handle(View view, int color) {
        view.setBackgroundColor(color);
    }
}
