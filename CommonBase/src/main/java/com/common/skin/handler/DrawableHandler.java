package com.common.skin.handler;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.common.skin.api.SkinHelper;
import com.common.skin.attr.SkinAttr;

/**
 * 图片处理器
 *
 * @author LiuFeng
 * @data 2021/8/24 18:29
 */
public abstract class DrawableHandler implements SkinHandler {

    @Override
    public void handle(View view, SkinAttr skinAttr) {
        int resId = skinAttr.attrValueResId;
        Drawable drawable = SkinHelper.getResourceManager().getDrawable(resId);
        if (drawable != null) {
            handle(view, drawable);
        }
    }

    protected abstract void handle(View view, Drawable drawable);
}
