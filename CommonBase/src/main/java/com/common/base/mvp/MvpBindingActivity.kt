package com.common.base.mvp

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.common.base.BaseActivity
import com.common.base.BasePresenter
import com.common.base.ability.IBaseView
import com.common.base.ability.IBinding
import com.common.base.ability.IPresenter

/**
 * MVP模式Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:19
 */
abstract class MvpBindingActivity<B : ViewBinding, P : BasePresenter<out IBaseView>> :
    BaseActivity(), IBinding<B>, IPresenter<P> {
    protected var binding: B? = null
    protected var mPresenter: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = getViewBinding()
        mPresenter = createPresenter()
        super.onCreate(savedInstanceState)
    }

    /**
     * 当binding非空时，用binding的rootView来设置；
     * 当binding为空时，则用布局id设置；
     *
     * @param layoutResID
     */
    override fun setContentView(layoutResID: Int) {
        if (binding != null) {
            setContentView(binding!!.root)
        } else {
            super.setContentView(layoutResID)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestroy()
    }
}