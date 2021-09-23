package com.common.skin.callback;

import android.view.View;

import com.common.skin.api.SkinItem;


/**
 * 皮肤拦截器（根据不同需求执行皮肤分发拦截）
 *
 * @author LiuFeng
 * @data 2021/9/7 17:20
 */
public interface SkinInterceptor {

    /**
     * 返回true则执行拦截
     *
     * @param view
     * @param skinItem
     * @return
     */
    boolean intercept(View view, SkinItem skinItem);
}
