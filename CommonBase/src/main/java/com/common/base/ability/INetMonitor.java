package com.common.base.ability;

import com.common.receiver.NetworkStateReceiver;
import com.common.utils.NetworkUtil;

/**
 * 联网状态监视器接口
 *
 * @author LiuFeng
 * @data 2021/10/15 15:13
 */
public interface INetMonitor extends NetworkStateReceiver.NetworkStateChangedListener {

    /**
     * 创建事件通知
     */
    default void createNetMonitor() {
        if (openMonitor()) {
            NetworkStateReceiver.getInstance().addNetworkStateChangedListener(this);
        }
    }

    /**
     * 销毁事件通知
     */
    default void destroyNetMonitor() {
        if (openMonitor()) {
            NetworkStateReceiver.getInstance().removeNetworkStateChangedListener(this);
        }
    }

    /**
     * 是否打开监视器
     *
     * @return
     */
    default boolean openMonitor() {
        return true;
    }

    /**
     * 判断网络是否可用
     *
     * @return
     */
    default boolean isNetAvailable() {
        return NetworkUtil.isNetAvailable();
    }
}
