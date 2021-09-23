package com.common.skin.handler;

import android.view.View;

import com.common.skin.attr.SkinAttr;

/**
 * 皮肤属性处理器接口
 *
 * @author LiuFeng
 * @data 2021/8/24 18:28
 */
public interface SkinHandler {

    void handle(View view, SkinAttr skinAttr);
}
