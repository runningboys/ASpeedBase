package com.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.common.base.BaseApp
import com.common.utils.log.LogUtil

/**
 * 时间广播
 *
 * @author LiuFeng
 * @data 2021/2/23 10:12
 */
object TimeReceiver : BroadcastReceiver() {
    private const val TAG = "TimeReceiver"
    private val listeners = mutableListOf<TimeListener>()
    private var isRegister = false

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (Intent.ACTION_TIME_CHANGED == action || Intent.ACTION_DATE_CHANGED == action) {
            LogUtil.i(TAG, "date time changed!")
            listeners.forEach { it.onTimeChange() }
        }
    }

    fun register() {
        if (!isRegister) {
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_TIME_CHANGED)
            filter.addAction(Intent.ACTION_DATE_CHANGED)
            BaseApp.context.registerReceiver(this, filter)
            isRegister = true
        }
    }

    fun unregister() {
        if (isRegister) {
            BaseApp.context.unregisterReceiver(this)
            isRegister = false
        }
    }

    fun addListener(listener: TimeListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: TimeListener) {
        listeners.remove(listener)
    }
}


interface TimeListener {
    fun onTimeChange()
}