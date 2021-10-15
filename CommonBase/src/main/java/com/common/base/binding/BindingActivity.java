package com.common.base.binding;

import android.os.Bundle;

import com.common.base.BaseActivity;
import com.common.base.ability.IBinding;

import androidx.viewbinding.ViewBinding;

/**
 * 带Binding的基类Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
public abstract class BindingActivity<B extends ViewBinding> extends BaseActivity implements IBinding<B> {

    protected B binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.binding = getViewBinding();
        super.onCreate(savedInstanceState);
    }

    /**
     * 当binding非空时，用binding的rootView来设置；
     * 当binding为空时，则用布局id设置；
     *
     * @param layoutResID
     */
    @Override
    public void setContentView(int layoutResID) {
        if (binding != null) {
            setContentView(binding.getRoot());
        } else {
            super.setContentView(layoutResID);
        }
    }
}
