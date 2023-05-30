package com.common.base.mvvm

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.common.base.BaseActivity
import com.common.base.ability.IBinding
import com.common.base.ability.IViewModel

/**
 * MVVM模式Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
abstract class MvvmBindingActivity<B : ViewBinding, V : ViewModel> : BaseActivity(), IBinding<B>,
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
     * @param layoutResID
     */
    override fun setContentView(layoutResID: Int) {
        if (binding != null) {
            setContentView(binding!!.root)
        } else {
            super.setContentView(layoutResID)
        }
    }
}