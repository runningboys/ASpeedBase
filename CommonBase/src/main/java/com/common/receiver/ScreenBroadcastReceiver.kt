package com.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager

/**
 * 屏幕状态广播接收者
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
class ScreenBroadcastReceiver : BroadcastReceiver() {
    private val listeners = mutableListOf<ScreenStateListener>()

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_SCREEN_ON == intent.action) {
            listeners.forEach { it.onScreenOn() }
        } else if (Intent.ACTION_SCREEN_OFF == intent.action) {
            listeners.forEach { it.onScreenOff() }
        } else if (Intent.ACTION_USER_PRESENT == intent.action) {
            listeners.forEach { it.onScreenUnlock() }
        }
    }

    /**
     * 获取屏幕状态
     *
     * @param context
     */
    private fun getScreenState(context: Context) {
        val manager = context.getSystemService(Context.POWER_SERVICE) as PowerManager? ?: return
        if (manager.isScreenOn) {
            listeners.forEach { it.onScreenOn() }
        } else {
            listeners.forEach { it.onScreenOff() }
        }
    }

    /**
     * 注册
     *
     * @param context
     */
    private fun register(context: Context) {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        context.registerReceiver(instance, filter)
        getScreenState(context)
    }

    /**
     * 取消注册
     *
     * @param context
     */
    fun unregister(context: Context) {
        context.unregisterReceiver(instance)
    }

    fun addListener(listener: ScreenStateListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ScreenStateListener) {
        listeners.remove(listener)
    }


    /**
     * 屏幕状态监听器
     */
    interface ScreenStateListener {
        /**
         * 亮屏
         */
        fun onScreenOn()

        /**
         * 锁屏
         */
        fun onScreenOff()

        /**
         * 解锁
         */
        fun onScreenUnlock()
    }

    companion object {
        val instance = ScreenBroadcastReceiver()
    }
}