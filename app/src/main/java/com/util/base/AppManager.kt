package com.util.base

import android.content.Context
import com.common.utils.crash.AppCrashHandler
import com.common.utils.crash.CrashStrategy
import com.common.utils.log.LogUtil
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
     * 服务器环境配置（根据打包配置自动匹配环境）
     */
    private var serverConfig = ServerEnum.Beta

    /**
     * 初始化
     *
     * @param context
     */
    fun init(context: Context) {
        // 日志
        initLogger(context)

        // 崩溃
        initCrashHandler(context)

        LogUtil.d("init --> 环境：$serverConfig")
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
     * 获取base地址
     *
     * @return
     */
    fun getMeetingUrl(): String {
        return Config.meetingUrl
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
     * url配置类
     */
    private object Config {
        var baseUrl: String
        var meetingUrl: String

        // 初始化配置数据
        init {
            // 取不同环境地址
            when (serverConfig) {
                ServerEnum.Release -> {
                    baseUrl = AppConstants.Release.BASE_URL
                    meetingUrl = AppConstants.Release.MEETING_URL
                }
                ServerEnum.Beta -> {
                    baseUrl = AppConstants.Beta.BASE_URL
                    meetingUrl = AppConstants.Beta.MEETING_URL
                }
                else -> {
                    baseUrl = AppConstants.Develop.BASE_URL
                    meetingUrl = AppConstants.Develop.MEETING_URL
                }
            }
        }
    }
}