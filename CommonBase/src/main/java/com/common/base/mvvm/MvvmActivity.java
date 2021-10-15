package com.common.base.mvvm;

import android.os.Bundle;

import com.common.base.BaseActivity;
import com.common.base.ability.IViewModel;

import androidx.lifecycle.ViewModel;

/**
 * MVVM模式Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
public abstract class MvvmActivity<V extends ViewModel> extends BaseActivity implements IViewModel<V> {

    protected V viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.viewModel = this.createViewModel();
        super.onCreate(savedInstanceState);
    }
}
