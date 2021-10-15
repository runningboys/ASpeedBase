package com.common.base.mvvm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.common.base.BaseFragment;
import com.common.base.ability.IBinding;
import com.common.base.ability.IViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.viewbinding.ViewBinding;

/**
 * MVVM模式Fragment
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
public abstract class MvvmBindingFragment<B extends ViewBinding, V extends ViewModel> extends BaseFragment implements IBinding<B>, IViewModel<V> {

    protected B binding;
    protected V viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.binding = getViewBinding();
        this.viewModel = this.createViewModel();
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
