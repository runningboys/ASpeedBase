package com.common.utils.bus

import com.common.utils.thread.UIHandler
import java.util.Queue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * 观察者主题
 *
 * @author LiuFeng
 * @data 2020/9/2 10:21
 */
open class SubjectImpl : Subject {
    private val observerMap = ConcurrentHashMap<Class<out IEvent>, Queue<Observer<out IEvent>>>()


    /**
     * 添加指事件的观察者。
     *
     * @param event
     * @param observer
     */
    override fun <T : IEvent> on(event: Class<T>, observer: (data: T) -> Unit) {
        on(event, object : Observer<T> {
            override fun update(data: T) {
                observer(data)
            }
        })
    }


    /**
     * 添加指事件的观察者。
     *
     * @param event
     * @param observer
     */
    @Synchronized
    override fun <T : IEvent> on(event: Class<T>, observer: Observer<T>) {
        var observers = observerMap[event]
        if (observers == null) {
            observers = ConcurrentLinkedQueue()
            val tempObservers = observerMap.putIfAbsent(event, observers)
            if (tempObservers != null) {
                observers = tempObservers
            }
        }
        observers.add(observer)
        observerMap[event] = observers
    }


    /**
     * 移除事件。
     *
     * @param event
     */
    @Synchronized
    override fun <T : IEvent> off(event: Class<T>) {
        observerMap.remove(event)
    }


    /**
     * 移除事件的观察者。
     *
     * @param event
     * @param observer
     */
    @Synchronized
    override fun <T : IEvent> off(event: Class<T>, observer: Observer<T>) {
        val observers = observerMap[event]
        if (observers != null) {
            val iterator = observers.iterator()
            while (iterator.hasNext()) {
                val t = iterator.next()
                if (observer == t) {
                    iterator.remove()
                    break
                }
            }
        }
    }

    override fun clearAll() {
        observerMap.clear()
    }


    /**
     * 通知观察者有新状态更新。
     *
     * @param event
     * @param <T>
     */
    @Synchronized
    override fun <T : IEvent> notify(event: T) {
        val observers = observerMap[event.javaClass]
        if (observers != null) {
            UIHandler.run {
                for (observer in observers) {
                    (observer as Observer<IEvent>).update(event)
                }
            }
        }
    }
}