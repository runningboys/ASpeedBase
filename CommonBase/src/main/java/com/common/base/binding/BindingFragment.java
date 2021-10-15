package com.common.base.binding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.common.base.BaseFragment;
import com.common.base.ability.IBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

/**
 * 带Binding的基类Fragment
 *
 * @author LiuFeng
 * @data 2021/9/22 16:19
 */
public abstract class BindingFragment<B extends ViewBinding> extends BaseFragment implements IBinding<B> {

    protected B binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.binding = getViewBinding();
        super.onCreate(savedInstanceState);
    }

    /**
     * 当binding非空时，用binding的rootView来设置；
     * 当binding为空时，则用布局id设置；
     *
     * @param inflater
     * @param container
     */
    @Override
    protected void setContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        if (binding != null) {
            this.mRootView = binding.getRoot();
        } else {
            super.setContentView(inflater, container);
        }
    }
}
