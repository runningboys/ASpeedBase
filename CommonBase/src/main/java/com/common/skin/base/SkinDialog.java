package com.common.skin.base;

import android.content.Context;
import android.view.Window;


import com.common.skin.api.SkinManager;

import androidx.appcompat.app.AppCompatDialog;

/**
 * 封装皮肤处理的弹窗
 *
 * @author LiuFeng
 * @data 2021/9/9 16:34
 */
public abstract class SkinDialog extends AppCompatDialog {


    public SkinDialog(Context context) {
        super(context);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /*@NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        LayoutInflater layoutInflater = super.getLayoutInflater();
        SkinLayoutInflaterFactory.load(layoutInflater);
        return layoutInflater;
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        SkinManager.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SkinManager.getInstance().unRegister(this);
    }
}
