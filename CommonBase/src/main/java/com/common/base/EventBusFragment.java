package com.common.base;

import android.os.Bundle;

import com.common.eventbus.Event;
import com.common.eventbus.EventBusUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 带事件Fragment
 *
 * @author LiuFeng
 * @data 2021/4/28 14:35
 */
public abstract class EventBusFragment extends Fragment {
    private boolean isDestroyed;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isDestroyed = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (openEventBus()) {
            EventBusUtil.register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroyed = true;

        if (openEventBus()) {
            EventBusUtil.unregister(this);
        }
    }

    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，false不绑定
     */
    protected boolean openEventBus() {
        return true;
    }

    /**
     * 接收事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public <T> void onEventBusCome(Event<T> event) {
        if (isDestroyed) return;

        if (event != null) {
            onMessageEvent(event.eventName, event.data);
        }
    }

    /**
     * 接收粘性事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public <T> void onStickyEventBusCome(Event<T> event) {
        if (isDestroyed) return;

        if (event != null) {
            onMessageStickyEvent(event.eventName, event.data);
        }
    }

    /**
     * 子类重写接收事件
     */
    public <T> void onMessageEvent(String eventName, T data) {

    }

    /**
     * 子类重写接收粘性事件
     */
    public <T> void onMessageStickyEvent(String eventName, T data) {

    }
}