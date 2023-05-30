package com.common.crash

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.TextUtils
import com.common.utils.AppUtil
import com.common.utils.DeviceUtil
import com.common.utils.log.LogUtil
import com.umeng.analytics.MobclickAgent
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
@SuppressLint("StaticFieldLeak")
object AppCrashHandler : Thread.UncaughtExceptionHandler {
    private const val FILE_NAME = "crash.log"
    private var mLogPath: String? = null
    private var mContext: Context? = null

    /**
     * 初始化
     *
     * @param context
     */
    fun init(context: Context, logPath: String?) {
        mContext = context.applicationContext
        mLogPath = logPath
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        // 保存错误信息到SDCard
        saveExceptionToSDCard(ex, mLogPath)

        // 上传错误信息到服务器
        uploadExceptionToServer(ex)

        // 打印错误信息到控制台
        LogUtil.e("AppCrashHandler:" + ex.message, ex)
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
            pw.println("App版本号：" + AppUtil.getVersionCode(mContext))
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
     * 上传错误信息到服务器
     *
     * @param ex
     */
    private fun uploadExceptionToServer(ex: Throwable) {
        // 上传到友盟
        MobclickAgent.reportError(mContext, ex)
    }
}