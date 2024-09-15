package com.common.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils.OnAppStatusChangedListener
import com.common.receiver.NetworkStateReceiver
import com.common.receiver.TimeReceiver
import com.common.receiver.USBStateListener
import com.common.receiver.USBStateReceiver
import com.common.utils.log.LogUtil
import com.common.utils.ui.RouterUtil

/**
 * Application基类
 */
open class BaseApp : MultiDexApplication() {

    companion object {
        @JvmStatic
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        // 初始化ARouter
        RouterUtil.init(this)

        // 注册时间改变广播
        TimeReceiver.register()

        // 注册网络变化广播
        NetworkStateReceiver.register()

        // USB状态变化广播
        USBStateReceiver.register()

        // 添加USB状态变化监听
        USBStateReceiver.addListener(object : USBStateListener {
            override fun onUSBStateChanged(isUSBConnecting: Boolean) {
                LogUtil.setUsbConnect(isUSBConnecting)
            }
        })


        // 应用前后台监听
        AppUtils.registerAppStatusChangedListener(object : OnAppStatusChangedListener {
            override fun onForeground(activity: Activity) {
                onAppForeground(activity)
            }

            override fun onBackground(activity: Activity) {
                onAppBackground(activity)
            }
        })
    }

    protected open fun onAppForeground(activity: Activity) {
        LogUtil.d("App onForeground")
    }

    protected open fun onAppBackground(activity: Activity) {
        LogUtil.d("App onBackground")
    }
}