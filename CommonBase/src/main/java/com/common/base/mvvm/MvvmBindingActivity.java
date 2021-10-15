package com.common.base.mvvm;

import android.os.Bundle;

import com.common.base.BaseActivity;
import com.common.base.ability.IBinding;
import com.common.base.ability.IViewModel;

import androidx.lifecycle.ViewModel;
import androidx.viewbinding.ViewBinding;

/**
 * MVVM模式Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
public abstract class MvvmBindingActivity<B extends ViewBinding, V extends ViewModel> extends BaseActivity implements IBinding<B>, IViewModel<V> {

    protected B binding;
    protected V viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.binding = getViewBinding();
        this.viewModel = this.createViewModel();
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
