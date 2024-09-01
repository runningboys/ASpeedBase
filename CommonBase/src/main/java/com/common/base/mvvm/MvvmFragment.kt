package com.common.base.mvvm

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.common.base.BaseFragment

/**
 * MVVM模式Fragment
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
abstract class MvvmFragment<VM : ViewModel> : BaseFragment(), IViewModel<VM> {
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = this.createViewModel()
        super.onCreate(savedInstanceState)
    }
}