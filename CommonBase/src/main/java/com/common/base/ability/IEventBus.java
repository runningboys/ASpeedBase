package com.common.base.ability;

import com.common.eventbus.EventBusUtil;


/**
 * EventBus集成接口
 *
 * @author LiuFeng
 * @data 2021/10/15 14:36
 */
public interface IEventBus {

    /**
     * 创建事件通知
     */
    default void createEventBus() {
        if (openEventBus()) {
            EventBusUtil.register(this);
        }
    }

    /**
     * 销毁事件通知
     */
    default void destroyEventBus() {
        if (openEventBus()) {
            EventBusUtil.unregister(this);
        }
    }

    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，false不绑定
     */
    default boolean openEventBus() {
        return true;
    }

    /**
     * 接收普通事件
     */
    <T> void onMessageEvent(String eventName, T data);

    /**
     * 接收粘性事件
     */
    <T> void onMessageStickyEvent(String eventName, T data);
}
