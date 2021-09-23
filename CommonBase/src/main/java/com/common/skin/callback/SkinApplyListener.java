package com.common.skin.callback;

import android.view.View;

import com.common.skin.api.SkinItem;


/**
 * 皮肤应用监听
 *
 * @author LiuFeng
 * @data 2021/9/7 17:20
 */
public interface SkinApplyListener {

    void onApply(View view, SkinItem skinItem);
}
