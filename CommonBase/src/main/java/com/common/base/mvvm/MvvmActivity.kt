package com.common.base.mvvm

import androidx.lifecycle.ViewModel
import com.common.base.BaseActivity

/**
 * MVVM模式Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
abstract class MvvmActivity<VM : ViewModel> : BaseActivity() {
    protected abstract val viewModel: VM
}