package com.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.common.base.BaseApp

/**
 * USB状态广播接收者
 *
 * @author LiuFeng
 * @data 2018/10/9 20:04
 */
object USBStateReceiver : BroadcastReceiver() {
    private const val ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE"
    private const val USB_CONNECTED = "connected"
    private val mListeners = mutableListOf<USBStateListener>()
    private var isRegister = false

    /**
     * 广播接收者回调方法
     *
     * @param context
     * @param intent
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_USB_STATE == intent.action) {
            if (intent.extras != null) {
                val isUSBConnecting = intent.extras!!.getBoolean(USB_CONNECTED)
                for (listener in mListeners) {
                    listener.onUSBStateChanged(isUSBConnecting)
                }
            }
        }
    }

    /**
     * 注册
     */
    @Synchronized
    fun register() {
        if (!isRegister) {
            isRegister = true
            val filter = IntentFilter()
            filter.addAction(ACTION_USB_STATE)
            BaseApp.context.registerReceiver(this, filter)
        }
    }

    /**
     * 取消注册
     */
    @Synchronized
    fun unregister() {
        isRegister = false
        BaseApp.context.unregisterReceiver(this)
    }

    /**
     * 添加状态变化监听器
     *
     * @param listener
     */
    fun addListener(listener: USBStateListener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener)
        }
    }

    /**
     * 移除状态变化监听器
     *
     * @param listener
     */
    fun removeListener(listener: USBStateListener) {
        mListeners.remove(listener)
    }
}


interface USBStateListener {

    /**
     * USB连接状态有变化
     *
     * @param isUSBConnecting USB连接中
     */
    fun onUSBStateChanged(isUSBConnecting: Boolean)
}
