package com.common.base.mvvm;

import android.os.Bundle;

import com.common.base.BaseFragment;
import com.common.base.ability.IViewModel;

import androidx.lifecycle.ViewModel;

/**
 * MVVM模式Fragment
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
public abstract class MvvmFragment<V extends ViewModel> extends BaseFragment implements IViewModel<V> {

    protected V viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.viewModel = this.createViewModel();
        super.onCreate(savedInstanceState);
    }
}
