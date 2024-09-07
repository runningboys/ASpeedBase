package com.common.base

import android.annotation.SuppressLint
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.common.receiver.NetworkStateReceiver
import com.common.utils.ui.RouterUtil

/**
 * Application基类
 */
open class BaseApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        context = this

        // 初始化ARouter
        RouterUtil.init(this)

        // 注册网络状态变化广播接收器
        NetworkStateReceiver.instance.register(this)
    }

    companion object {
        @JvmStatic
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}