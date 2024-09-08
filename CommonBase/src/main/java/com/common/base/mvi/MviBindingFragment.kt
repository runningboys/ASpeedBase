package com.common.base.mvi

import androidx.viewbinding.ViewBinding
import com.common.base.binding.BindingFragment

/**
 * MVI模式Fragment
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
abstract class MviBindingFragment<VB : ViewBinding, VM : IntentViewModel<out IUIState, out IUiIntent>> : BindingFragment<VB>() {
    protected abstract val viewModel: VM
}