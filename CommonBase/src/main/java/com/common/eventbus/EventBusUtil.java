package com.common.eventbus;

import androidx.annotation.NonNull;
import com.common.utils.log.LogUtil;
import org.greenrobot.eventbus.EventBus;

/**
 * EventBus事件通知工具类
 *
 * @author LiuFeng
 * @data 2020/1/9 11:26
 */
public class EventBusUtil {

    /**
     * 注册
     *
     * @param subscriber
     */
    public static void register(Object subscriber) {
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().register(subscriber);
        }
    }

    /**
     * 取消注册
     *
     * @param subscriber
     */
    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    /**
     * 发送事件
     *
     * @param eventName
     */
    public static void post(@NonNull String eventName) {
        LogUtil.d("发送事件 --> event: " + eventName);
        EventBus.getDefault().post(new Event<String>(eventName));
    }

    /**
     * 发送事件
     *
     * @param eventName
     * @param data
     * @param <T>
     */
    public static <T> void post(@NonNull String eventName, T data) {
        LogUtil.d("发送事件 --> event: " + eventName);
        EventBus.getDefault().post(new Event<T>(eventName, data));
    }

    /**
     * 发送粘性事件
     *
     * @param eventName
     */
    public static void postSticky(@NonNull String eventName) {
        LogUtil.d("发送粘性事件 --> event: " + eventName);
        EventBus.getDefault().postSticky(new Event<String>(eventName));
    }

    /**
     * 发送粘性事件
     *
     * @param eventName
     * @param data
     * @param <T>
     */
    public static <T> void postSticky(@NonNull String eventName, T data) {
        EventBus.getDefault().postSticky(new Event<T>(eventName, data));
    }
}
