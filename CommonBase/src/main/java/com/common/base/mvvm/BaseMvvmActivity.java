package com.common.base.mvvm;

import android.os.Bundle;
import com.common.base.BaseActivity;
import com.common.base.BaseViewModel;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * MVVM模式Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
public abstract class BaseMvvmActivity<B extends ViewDataBinding, V extends ViewModel> extends BaseActivity {

    protected B binding;
    protected V viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, getContentViewId());
        this.viewModel = this.createViewModel();
        super.onCreate(savedInstanceState);
    }

    /**
     * 创建ViewModel
     *
     * @return
     */
    protected abstract V createViewModel();

    /**
     * 创建ViewModel
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends BaseViewModel> T createViewModel(Class<T> clazz) {
        return new ViewModelProvider(this).get(clazz);
    }
}
