package com.common.base.mvvm

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.common.base.BaseActivity
import com.common.base.ability.IViewModel

/**
 * MVVM模式Activity
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
abstract class MvvmActivity<V : ViewModel> : BaseActivity(), IViewModel<V> {
    protected var viewModel: V? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = this.createViewModel()
        super.onCreate(savedInstanceState)
    }
}