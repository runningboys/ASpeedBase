package com.common.base.mvi

import androidx.viewbinding.ViewBinding
import com.common.base.binding.BindingActivity

/**
 * MVI模式Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
abstract class MviBindingActivity<VB : ViewBinding, VM : IntentViewModel<out IUIState, out IUiIntent>> : BindingActivity<VB>() {
    protected abstract val viewModel: VM
}