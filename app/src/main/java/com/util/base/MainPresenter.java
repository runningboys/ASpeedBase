package com.util.base;

import android.content.Context;


public class MainPresenter extends MainContract.Presenter {

    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public MainPresenter(Context context, MainContract.View view) {
        super(context, view);
    }

    @Override
    public void refreshToken() {

    }
}