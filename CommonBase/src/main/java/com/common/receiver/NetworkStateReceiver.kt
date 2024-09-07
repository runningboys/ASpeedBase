package com.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.common.utils.network.NetworkUtil

/**
 * 网络状态广播接收者
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
class NetworkStateReceiver private constructor() : BroadcastReceiver() {
    /**
     * 网络状态变化监听器
     */
    private val mListeners = mutableListOf<NetworkStateChangedListener>()

    /**
     * 广播接收者回调方法
     *
     * @param context
     * @param intent
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
            val isNetAvailable = NetworkUtil.isNetAvailable()
            mListeners.forEach { it.onNetworkStateChanged(isNetAvailable) }
        }
    }

    /**
     * 添加网络状态变化监听器
     *
     * @param listener
     */
    fun addListener(listener: NetworkStateChangedListener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener)
        }
    }

    /**
     * 移除网络状态变化监听器
     *
     * @param listener
     */
    fun removeListener(listener: NetworkStateChangedListener) {
        mListeners.remove(listener)
    }

    /**
     * 网络状态变化监听器
     */
    interface NetworkStateChangedListener {
        /**
         * 网络状态有变化
         *
         * @param isNetAvailable 网络是否可用
         */
        fun onNetworkStateChanged(isNetAvailable: Boolean)
    }

    /**
     * 注册
     *
     * @param context
     */
    fun register(context: Context) {
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(this, filter)
    }

    /**
     * 取消注册
     *
     * @param context
     */
    fun unregister(context: Context) {
        context.unregisterReceiver(this)
    }

    companion object {
        /**
         * 单例
         *
         * @return
         */
        val instance = NetworkStateReceiver()
    }
}