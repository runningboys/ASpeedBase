package com.common.utils.eventbus

import com.common.utils.log.LogUtil
import org.greenrobot.eventbus.EventBus

/**
 * EventBus事件通知工具类
 *
 * @author LiuFeng
 * @data 2020/1/9 11:26
 */
object EventBusUtil {
    /**
     * 注册
     *
     * @param subscriber
     */
    fun register(subscriber: Any) {
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().register(subscriber)
        }
    }

    /**
     * 取消注册
     *
     * @param subscriber
     */
    fun unregister(subscriber: Any) {
        EventBus.getDefault().unregister(subscriber)
    }

    /**
     * 发送事件
     *
     * @param eventName
     */
    fun post(eventName: String) {
        LogUtil.d("发送事件 --> event: $eventName")
        EventBus.getDefault().post(Event<String>(eventName))
    }

    /**
     * 发送事件
     *
     * @param eventName
     * @param data
     * @param <T>
    </T> */
    fun <T> post(eventName: String, data: T) {
        LogUtil.d("发送事件 --> event: $eventName")
        EventBus.getDefault().post(Event(eventName, data))
    }

    /**
     * 发送事件
     *
     * @param eventName
     * @param data
     * @param <T>
    </T> */
    fun <T> post(eventName: String, vararg data: T) {
        LogUtil.d("发送事件 --> event: $eventName")
        EventBus.getDefault().post(Event(eventName, *data))
    }

    /**
     * 发送粘性事件
     *
     * @param eventName
     */
    fun postSticky(eventName: String) {
        LogUtil.d("发送粘性事件 --> event: $eventName")
        EventBus.getDefault().postSticky(Event<String>(eventName))
    }

    /**
     * 发送粘性事件
     *
     * @param eventName
     * @param data
     * @param <T>
    </T> */
    fun <T> postSticky(eventName: String, data: T) {
        EventBus.getDefault().postSticky(Event(eventName, data))
    }

    /**
     * 发送粘性事件
     *
     * @param eventName
     * @param data
     * @param <T>
    </T> */
    fun <T> postSticky(eventName: String, vararg data: T) {
        EventBus.getDefault().postSticky(Event(eventName, *data))
    }
}