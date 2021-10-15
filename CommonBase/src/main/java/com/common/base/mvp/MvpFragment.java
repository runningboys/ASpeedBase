package com.common.base.mvp;

import android.os.Bundle;
import android.view.View;

import com.common.base.BaseFragment;
import com.common.base.BasePresenter;
import com.common.base.ability.IPresenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * MVP模式Fragment
 *
 * @author LiuFeng
 * @data 2021/10/15 11:48
 */
public abstract class MvpFragment<P extends BasePresenter> extends BaseFragment implements IPresenter<P> {
    protected P mPresenter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.mPresenter = this.createPresenter();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mPresenter != null) {
            this.mPresenter.onDestroy();
        }
    }
}
