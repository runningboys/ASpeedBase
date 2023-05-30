package com.common.base.mvp

import android.os.Bundle
import android.view.View
import com.common.base.BaseFragment
import com.common.base.BasePresenter
import com.common.base.ability.IBaseView
import com.common.base.ability.IPresenter

/**
 * MVP模式Fragment
 *
 * @author LiuFeng
 * @data 2021/10/15 11:48
 */
abstract class MvpFragment<P : BasePresenter<out IBaseView>> : BaseFragment(), IPresenter<P> {
    protected var mPresenter: P? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mPresenter = createPresenter()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestroy()
    }
}