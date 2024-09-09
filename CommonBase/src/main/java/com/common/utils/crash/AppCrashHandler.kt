package com.common.utils.crash

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.blankj.utilcode.util.AppUtils
import com.common.utils.log.LogUtil
import com.common.utils.resource.AppUtil
import com.common.utils.resource.DeviceUtil
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 崩溃处理
 *
 * @author LiuFeng
 * @date 2018-6-21
 */
object AppCrashHandler : Thread.UncaughtExceptionHandler {
    private const val FILE_NAME = "crash.log"
    private lateinit var mLogPath: String
    private lateinit var strategy: CrashStrategy
    private var defHandler: Thread.UncaughtExceptionHandler? = null

    /**
     * 初始化
     */
    fun init(logPath: String, strategy: CrashStrategy = CrashStrategy.ExitsApp) {
        mLogPath = logPath
        this.strategy = strategy
        defHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        if (strategy == CrashStrategy.RecoverApp) catchMainLooper()
    }

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        // 保存错误信息到SDCard
        saveExceptionToSDCard(ex, mLogPath)

        // 打印错误信息到控制台
        LogUtil.e("AppCrashHandler:" + ex.message, ex)



        when (strategy) {
            // 退出应用
            CrashStrategy.ExitsApp -> {
                AppUtils.exitApp()
            }

            // 重启应用
            CrashStrategy.RelaunchApp -> {
                AppUtils.relaunchApp(true)
            }

            // 恢复应用
            CrashStrategy.RecoverApp -> {
                // 非主线程，调用原始的uncaughtExceptionHandler进行处理
                if (thread != Looper.getMainLooper().thread) {
                    defHandler?.uncaughtException(thread, ex)
                }
            }
        }
    }


    /**
     * 保存错误信息到SDCard
     *
     * @param ex
     */
    private fun saveExceptionToSDCard(ex: Throwable, logPath: String?) {
        try {
            if (TextUtils.isEmpty(logPath)) {
                return
            }
            val logFile = File(logPath + File.separator + FILE_NAME)
            if (!logFile.exists()) {
                logFile.createNewFile()
            }
            val pw = PrintWriter(BufferedWriter(FileWriter(logFile)))
            // 打印发生异常的时间
            pw.println()
            pw.println(
                "错误发生时间：" + SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss.SSS", Locale.US).format(
                    Date()
                )
            )
            pw.println()

            // 打印手机信息
            pw.println("App版本号：" + AppUtil.getVersionCode())
            pw.println()
            pw.println("Android系统版本号：" + DeviceUtil.getBuildVersion())
            pw.println("Android手机制造商：" + DeviceUtil.getPhoneManufacturer())
            pw.println("Android手机品牌：" + DeviceUtil.getPhoneBrand())
            pw.println("Android手机型号：" + DeviceUtil.getPhoneModel())
            val sb = StringBuilder()
            sb.append("Android手机CPU：")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (abi in DeviceUtil.getPhoneCPU()) {
                    sb.append(abi)
                    sb.append("，")
                }
            }
            sb.deleteCharAt(sb.length - 1)
            pw.println(sb.toString())
            pw.println()

            // 打印错误信息
            ex.printStackTrace(pw)
            pw.println()
            pw.close()
        } catch (e: IOException) {
            LogUtil.e(e)
        }
    }


    /**
     * 捕获主线程异常崩溃
     * 参考：https://juejin.cn/post/7200030796974063675
     */
    private fun catchMainLooper() {
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    //主线程的异常会从这里抛出
                    Looper.loop()
                } catch (ex: Throwable) {
                    // 保存错误信息到SDCard
                    saveExceptionToSDCard(ex, mLogPath)

                    // 打印错误信息到控制台
                    LogUtil.e("AppCrashHandler:" + ex.message, ex)
                }
            }
        }
    }
}

enum class CrashStrategy {
    ExitsApp, RelaunchApp, RecoverApp
}