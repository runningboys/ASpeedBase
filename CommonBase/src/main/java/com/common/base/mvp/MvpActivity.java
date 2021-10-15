package com.common.base.mvp;

import android.os.Bundle;
import com.common.base.BaseActivity;
import com.common.base.BasePresenter;
import com.common.base.ability.IPresenter;

/**
 * MVP模式Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:19
 */
public abstract class MvpActivity<P extends BasePresenter> extends BaseActivity implements IPresenter<P> {

    protected P mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.mPresenter = this.createPresenter();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mPresenter != null) {
            this.mPresenter.onDestroy();
        }
    }
}
