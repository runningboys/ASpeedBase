package com.common.base.mvp;

import android.os.Bundle;

import com.common.base.BaseActivity;
import com.common.base.BasePresenter;
import com.common.base.ability.IBaseView;
import com.common.base.ability.IBinding;
import com.common.base.ability.IPresenter;

import androidx.viewbinding.ViewBinding;

/**
 * MVP模式Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:19
 */
public abstract class MvpBindingActivity<B extends ViewBinding, P extends BasePresenter<? extends IBaseView>> extends BaseActivity implements IBinding<B>, IPresenter<P> {

    protected B binding;
    protected P mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.binding = getViewBinding();
        this.mPresenter = this.createPresenter();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mPresenter != null) {
            this.mPresenter.onDestroy();
        }
    }
}
