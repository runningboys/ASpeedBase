package com.common.utils.bus

/**
 * 观察者主题
 *
 * @author LiuFeng
 * @data 2020/9/2 10:21
 */
interface Subject {


    /**
     * 添加指事件的观察者。
     *
     * @param event
     * @param observer
     */
    fun <T : IEvent> on(event: Class<T>, observer: (data: T) -> Unit)


    /**
     * 添加指事件的观察者。
     *
     * @param event
     * @param observer
     */
    fun <T : IEvent> on(event: Class<T>, observer: Observer<T>)


    /**
     * 移除事件。
     *
     * @param event
     */
    fun <T : IEvent> off(event: Class<T>)


    /**
     * 移除事件的观察者。
     *
     * @param event
     * @param observer
     */
    fun <T : IEvent> off(event: Class<T>, observer: Observer<T>)


    /**
     * 清除所有事件。
     */
    fun clearAll()


    /**
     * 通知观察者有新状态更新。
     *
     * @param event
     * @param <T>
     */
    fun <T : IEvent> notify(event: T)
}