package com.common.base.mvp

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.common.base.ability.IBaseView
import com.common.base.binding.BindingActivity

/**
 * MVP模式Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:19
 */
abstract class MvpBindingActivity<P : BasePresenter<out IBaseView>, VB : ViewBinding> : BindingActivity<VB>(), IPresenter<P> {
    protected lateinit var mPresenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        mPresenter = createPresenter()
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestroy()
    }
}