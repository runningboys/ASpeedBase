package com.common.base.ability

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

/**
 * ViewModel集成接口
 *
 * @author LiuFeng
 * @data 2021/10/15 14:47
 */
interface IViewModel<V : ViewModel> : ViewModelStoreOwner {
    /**
     * 创建ViewModel
     *
     * @return
     */
    fun createViewModel(): V

    /**
     * 创建ViewModel
     *
     * @param clazz
     * @param <T>
     * @return
     */
    fun createViewModel(clazz: Class<V>): V {
        return ViewModelProvider(this).get(clazz)
    }
}