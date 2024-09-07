package com.common.base.mvvm

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.common.base.binding.BindingActivity

/**
 * MVVM模式Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
abstract class MvvmBindingActivity<VM : ViewModel, VB : ViewBinding> : BindingActivity<VB>() {
    protected abstract val viewModel: VM
}