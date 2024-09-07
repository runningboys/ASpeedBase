package com.common.base.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 基类ViewModel
 *
 * @author LiuFeng
 * @data 2021/3/12 16:25
 */
open class BaseViewModel : ViewModel() {

    /**
     * 启动协程
     */
    fun launch(
            context: CoroutineContext = EmptyCoroutineContext,
            start: CoroutineStart = CoroutineStart.DEFAULT,
            block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch(context, start, block)
    }
}