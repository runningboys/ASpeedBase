package com.common.skin.handler;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

/**
 * src处理器
 *
 * @author LiuFeng
 * @data 2021/8/24 18:28
 */
public class SrcHandler extends DrawableHandler {

    @Override
    protected void handle(View view, Drawable drawable) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(drawable);
            return;
        }

        if (view instanceof CompoundButton) {
            ((CompoundButton) view).setButtonDrawable(drawable);
        }
    }
}
