package com.common.base.ability

import androidx.lifecycle.Lifecycle

/**
 * view基类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
interface IBaseView {
    /**
     * 显示Loading框
     */
    fun showLoading()

    /**
     * 隐藏Loading框
     */
    fun hideLoading()

    /**
     * 显示消息
     *
     * @param message
     */
    fun showMessage(message: String)

    /**
     * 错误处理
     *
     * @param code
     * @param message
     */
    fun onError(code: Int, message: String?)

    /**
     * 获取生命周期
     */
    fun getLife(): Lifecycle
}