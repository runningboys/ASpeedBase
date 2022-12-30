package com.common.base.ability

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
}