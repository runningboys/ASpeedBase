package com.common.base.binding

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.common.base.BaseActivity

/**
 * 带Binding的基类Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
abstract class BindingActivity<VB : ViewBinding> : BaseActivity(), IBinding<VB> {
    protected lateinit var binding: VB


    override fun getLayoutId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = getViewBinding(this)
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        setContentView(binding.root)
    }
}