package com.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager

/**
 * 耳机广播接收器
 */
class HeadsetReceiver private constructor() : BroadcastReceiver() {
    private val listeners = mutableListOf<HeadsetListener>()
    private var isRegister = false

    /**
     * 注册耳机广播
     *
     * @param context
     */
    @Synchronized
    fun registerReceiver(context: Context) {
        if (!isRegister) {
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_HEADSET_PLUG)
            filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            context.registerReceiver(this, filter)
            isRegister = true
        }
    }

    /**
     * 解注册耳机广播
     *
     * @param context
     */
    @Synchronized
    fun unregisterReceiver(context: Context) {
        if (isRegister) {
            context.unregisterReceiver(this)
            isRegister = false
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_HEADSET_PLUG -> {
                val state = intent.getIntExtra("state", 0)
                listeners.forEach { it.onInOrOut(state) }
            }

            AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                listeners.forEach { it.onPullOut() }
            }
        }
    }

    /**
     * 添加耳机插拔监听
     *
     * @param listener
     */
    fun addListener(listener: HeadsetListener) {
        listeners.add(listener)
    }

    /**
     * 移除耳机插拔监听
     *
     * @param listener
     */
    fun removeListener(listener: HeadsetListener) {
        listeners.remove(listener)
    }

    /**
     * 耳机插拔监听器
     */
    interface HeadsetListener {
        /**
         * 耳机插入
         *
         * @param state
         */
        fun onInOrOut(state: Int)

        /**
         * 耳机拔出
         */
        fun onPullOut()
    }

    companion object {
        val instance = HeadsetReceiver()
    }
}