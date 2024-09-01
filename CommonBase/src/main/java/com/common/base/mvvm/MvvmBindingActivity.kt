package com.common.base.mvvm

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.common.base.binding.BindingActivity

/**
 * MVVM模式Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
abstract class MvvmBindingActivity<VM : ViewModel, VB : ViewBinding> : BindingActivity<VB>(), IViewModel<VM> {
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = this.createViewModel()
        super.onCreate(savedInstanceState)
    }
}