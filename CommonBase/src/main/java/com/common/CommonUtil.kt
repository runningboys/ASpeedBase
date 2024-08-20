package com.common

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.common.receiver.NetworkStateReceiver
import com.common.utils.RouterUtil

/**
 * Util初始化管理类
 *
 * @author liufeng
 * @date 2017-11-13
 */
@SuppressLint("StaticFieldLeak")
object CommonUtil {
    /**
     * 获取上下文
     *
     * @return
     */
    private lateinit var context: Context

    /**
     * 传入上下文，初始化数据
     *
     * @param application
     */
    fun init(application: Application) {
        context = application.applicationContext

        // 初始化ARouter
        RouterUtil.init(application)

        // 注册网络状态变化广播接收器
        NetworkStateReceiver.instance.register(context)
    }

    @JvmStatic
    fun getContext() = context
}