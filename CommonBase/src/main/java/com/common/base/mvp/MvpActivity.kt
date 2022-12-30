package com.common.base.mvp

import android.os.Bundle
import com.common.base.BaseActivity
import com.common.base.BasePresenter
import com.common.base.ability.IBaseView
import com.common.base.ability.IPresenter

/**
 * MVP模式Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:19
 */
abstract class MvpActivity<P : BasePresenter<out IBaseView>> : BaseActivity(), IPresenter<P> {
    protected var mPresenter: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        mPresenter = createPresenter()
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestroy()
    }
}