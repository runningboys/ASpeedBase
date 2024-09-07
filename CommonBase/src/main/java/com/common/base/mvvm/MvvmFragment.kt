package com.common.base.mvvm

import androidx.lifecycle.ViewModel
import com.common.base.BaseFragment

/**
 * MVVM模式Fragment
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
abstract class MvvmFragment<VM : ViewModel> : BaseFragment() {
    protected abstract val viewModel: VM
}