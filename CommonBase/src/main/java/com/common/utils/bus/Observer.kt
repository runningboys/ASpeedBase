package com.common.utils.bus

import java.io.Serializable

/**
 * 观察者
 *
 * @author LiuFeng
 * @data 2020/9/2 10:29
 */
interface Observer<T : IEvent> : Serializable {
    /**
     * 更新状态
     *
     * @param data
     */
    fun update(data: T)
}