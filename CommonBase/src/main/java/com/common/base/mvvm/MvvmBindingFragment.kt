package com.common.base.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.common.base.BaseFragment
import com.common.base.ability.IBinding
import com.common.base.ability.IViewModel

/**
 * MVVM模式Fragment
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
abstract class MvvmBindingFragment<B : ViewBinding, V : ViewModel> : BaseFragment(), IBinding<B>,
    IViewModel<V> {
    protected var binding: B? = null
    protected var viewModel: V? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = getViewBinding()
        viewModel = this.createViewModel()
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
}