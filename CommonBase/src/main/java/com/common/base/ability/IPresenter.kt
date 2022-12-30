package com.common.base.ability

import com.common.base.BasePresenter

/**
 * Presenter集成接口
 *
 * @author LiuFeng
 * @data 2021/10/15 15:49
 */
interface IPresenter<P : BasePresenter<out IBaseView>> {
    /**
     * 创建Presenter
     *
     * @return
     */
    fun createPresenter(): P
}