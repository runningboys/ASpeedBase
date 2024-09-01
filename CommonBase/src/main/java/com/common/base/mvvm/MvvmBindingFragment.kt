package com.common.base.mvvm

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.common.base.binding.BindingFragment

/**
 * MVVM模式Fragment
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
abstract class MvvmBindingFragment<VM : ViewModel, VB : ViewBinding> : BindingFragment<VB>(), IViewModel<VM> {
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = this.createViewModel()
        super.onCreate(savedInstanceState)
    }
}