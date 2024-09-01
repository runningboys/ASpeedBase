package com.common.base.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

/**
 * ViewModel集成接口
 *
 * @author LiuFeng
 * @data 2021/10/15 14:47
 */
interface IViewModel<VM : ViewModel> : ViewModelStoreOwner {
    /**
     * 创建ViewModel
     *
     * @return
     */
    fun createViewModel(): VM

    /**
     * 创建ViewModel
     *
     * @param clazz
     * @param <T>
     * @return
     */
    fun createViewModel(clazz: Class<VM>): VM {
        return ViewModelProvider(this).get(clazz)
    }
}