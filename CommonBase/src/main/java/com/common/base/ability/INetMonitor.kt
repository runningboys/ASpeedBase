package com.common.base.ability

import com.common.receiver.NetworkStateReceiver.NetworkStateChangedListener
import com.common.receiver.NetworkStateReceiver
import com.common.utils.NetworkUtil

/**
 * 联网状态监视器接口
 *
 * @author LiuFeng
 * @data 2021/10/15 15:13
 */
interface INetMonitor : NetworkStateChangedListener {
    /**
     * 创建网络监视器
     */
    fun createNetMonitor() {
        if (openMonitor()) {
            NetworkStateReceiver.getInstance().addNetworkStateChangedListener(this)
        }
    }

    /**
     * 销毁网络监视器
     */
    fun destroyNetMonitor() {
        if (openMonitor()) {
            NetworkStateReceiver.getInstance().removeNetworkStateChangedListener(this)
        }
    }

    /**
     * 是否打开监视器
     *
     * @return
     */
    fun openMonitor(): Boolean {
        return true
    }

    /**
     * 判断网络是否可用
     *
     * @return
     */
    val isNetAvailable: Boolean
        get() = NetworkUtil.isNetAvailable()
}