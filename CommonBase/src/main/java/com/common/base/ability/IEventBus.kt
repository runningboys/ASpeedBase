package com.common.base.ability

import com.common.eventbus.EventBusUtil

/**
 * EventBus集成接口
 *
 * @author LiuFeng
 * @data 2021/10/15 14:36
 */
interface IEventBus {
    /**
     * 创建事件通知
     */
    fun createEventBus() {
        if (openEventBus()) {
            EventBusUtil.register(this)
        }
    }

    /**
     * 销毁事件通知
     */
    fun destroyEventBus() {
        if (openEventBus()) {
            EventBusUtil.unregister(this)
        }
    }

    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，false不绑定
     */
    fun openEventBus(): Boolean {
        return true
    }

    /**
     * 接收普通事件
     */
    fun <T> onMessageEvent(eventName: String, data: T)

    /**
     * 接收粘性事件
     */
    fun <T> onMessageStickyEvent(eventName: String, data: T)
}