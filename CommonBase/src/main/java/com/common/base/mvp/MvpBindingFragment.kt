package com.common.base.mvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.common.base.BaseFragment
import com.common.base.BasePresenter
import com.common.base.ability.IBaseView
import com.common.base.ability.IBinding
import com.common.base.ability.IPresenter

/**
 * MVP模式Fragment
 *
 * @author LiuFeng
 * @data 2021/9/22 16:19
 */
abstract class MvpBindingFragment<B : ViewBinding, P : BasePresenter<out IBaseView>> :
    BaseFragment(), IBinding<B>, IPresenter<P> {
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
     * @param inflater
     * @param container
     */
    override fun setContentView(inflater: LayoutInflater, container: ViewGroup?) {
        if (binding != null) {
            mRootView = binding!!.root
        } else {
            super.setContentView(inflater, container)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestroy()
    }
}