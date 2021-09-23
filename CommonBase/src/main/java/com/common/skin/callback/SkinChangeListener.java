package com.common.skin.callback;


import com.common.skin.api.SkinItem;

/**
 * 皮肤切换后的监听回调
 *
 * @author LiuFeng
 * @data 2021/8/24 18:27
 */
public interface SkinChangeListener {

    void onSkinChange(SkinItem oldSkin, SkinItem newSkin);
}
