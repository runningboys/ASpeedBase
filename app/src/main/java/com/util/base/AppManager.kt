package com.util.base

import android.content.Context
import com.common.receiver.TimeListener
import com.common.receiver.TimeReceiver
import com.common.utils.crash.AppCrashHandler
import com.common.utils.crash.CrashStrategy
import com.common.utils.log.LogUtil
import com.common.utils.time.NetTimeUtil
import com.data.database.DBHelper
import com.data.network.manager.ApiManager
import com.data.preferences.AppSp
import com.util.base.common.AppConstants
import com.util.base.common.ServerEnum
import java.io.File

/**
 * 应用程序管理者
 *
 * @author LiuFeng
 * @data 2022/5/12 19:34
 */
object AppManager {

    /**
     * 是否是debug模式（日志打印控制）
     */
    private var isDebug = true

    /**
     * 服务器环境配置（用于简化切换正式、测试、开发等环境）
     */
    private var serverConfig = ServerEnum.Beta

    /**
     * 初始化
     *
     * @param context
     */
    fun init(context: Context) {
        LogUtil.d("init --> 环境：$serverConfig")

        // 日志
        initLogger(context)

        // 崩溃
        initCrashHandler(context)

        // 网络请求
        initApiManager()

        // 用户登录初始化
        loginInit()
    }


    /**
     * 用户登录初始化
     */
    fun loginInit() {
        // 判断是否已登录
        val userId = AppSp.getLoginUserId()
        if (userId.isBlank()) return

        // 记录登录用户
        AppSp.login(userId)

        // 初始化用户DB
        DBHelper.init(userId)

        // 网络对时
        initNetTime()
    }


    /**
     * 设置当前环境配置
     *
     * @param serverEnum
     */
    fun setServerConfig(serverEnum: ServerEnum) {
        serverConfig = serverEnum
    }


    /**
     * 获取base地址
     *
     * @return
     */
    fun getBaseUrl(): String {
        return Config.baseUrl
    }


    /**
     * 获取用户协议地址
     *
     * @return
     */
    fun getUserAgreementUrl(): String {
        return Config.userAgreementUrl
    }


    /**
     * 初始化日志工具
     */
    private fun initLogger(context: Context) {
        if (isDebug) {
            val logDir = File(context.getExternalFilesDir(""), "log")
            LogUtil.addDiskLogHandle(context, logDir.absolutePath)
            LogUtil.addCommonLogHandle()
            LogUtil.setLogTag("TestDemo")
            LogUtil.isLoggable = true
        } else {
            LogUtil.removeAllHandles()
            LogUtil.isLoggable = false
        }
    }


    /**
     * 初始化崩溃处理
     */
    private fun initCrashHandler(context: Context) {
        val crashDir = File(context.getExternalFilesDir(""), "crash")
        AppCrashHandler.init(crashDir.absolutePath, CrashStrategy.ExitsApp)
    }


    /**
     * 初始化ApiManager
     */
    private fun initApiManager() {
        ApiManager.defaultInstance().updateBaseUrl(getBaseUrl())
    }


    /**
     * 初始化网络对时
     */
    private fun initNetTime() {
        // 网络对时
        NetTimeUtil.calculateOffsetTime()

        // 时间改变监听
        TimeReceiver.addListener(object : TimeListener {
            override fun onTimeChange() {
                NetTimeUtil.calculateOffsetTime()
            }
        })
    }


    /**
     * url配置类
     */
    private object Config {
        var baseUrl: String
        var userAgreementUrl: String

        // 初始化配置数据
        init {
            // 取不同环境地址
            when (serverConfig) {
                ServerEnum.Release -> {
                    baseUrl = AppConstants.Release.BASE_URL
                    userAgreementUrl = AppConstants.Release.USER_AGREEMENT_URL
                }
                ServerEnum.Beta -> {
                    baseUrl = AppConstants.Beta.BASE_URL
                    userAgreementUrl = AppConstants.Beta.USER_AGREEMENT_URL
                }
                else -> {
                    baseUrl = AppConstants.Develop.BASE_URL
                    userAgreementUrl = AppConstants.Develop.USER_AGREEMENT_URL
                }
            }
        }
    }
}