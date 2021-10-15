package com.common.base.mvp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.common.base.BaseFragment;
import com.common.base.BasePresenter;
import com.common.base.ability.IBinding;
import com.common.base.ability.IPresenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

/**
 * MVP模式Fragment
 *
 * @author LiuFeng
 * @data 2021/9/22 16:19
 */
public abstract class MvpBindingFragment<B extends ViewBinding, P extends BasePresenter> extends BaseFragment implements IBinding<B>, IPresenter<P> {

    protected B binding;
    protected P mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.binding = getViewBinding();
        this.mPresenter = this.createPresenter();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mPresenter != null) {
            this.mPresenter.onDestroy();
        }
    }
}
