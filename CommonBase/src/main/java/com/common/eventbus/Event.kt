package com.common.eventbus

/**
 * EventBus的事件模型类
 *
 * @author LiuFeng
 * @data 2020/1/9 11:35
 */
class Event<T> {
    var eventName: String
    var data: T? = null
    var array: Array<out T>? = null

    constructor(eventName: String) {
        this.eventName = eventName
    }

    constructor(eventName: String, vararg data: T) {
        this.eventName = eventName
        array = data

        // 简化仅传一个参数时取值
        if (data.size == 1) {
            this.data = data[0]
        }
    }
}