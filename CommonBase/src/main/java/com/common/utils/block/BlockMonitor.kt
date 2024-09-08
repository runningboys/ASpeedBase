package com.common.utils.block

import android.os.Debug
import android.os.Looper
import android.util.Printer
import com.common.utils.log.LogUtil
import com.common.utils.log.StackTraceUtil
import com.common.utils.thread.ThreadUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * 卡顿检测器
 * 说明：最好是在开发中使用，不要在正式版本中使用，避免影响性能。
 *
 * @author LiuFeng
 * @data 2022/8/8 13:41
 */
object BlockMonitor {
    private const val TAG = "BlockMonitor"
    private lateinit var looperPrinter: LooperPrinter

    fun start() {
        if (this::looperPrinter.isInitialized) return
        val looper = Looper.getMainLooper()
        looperPrinter = LooperPrinter(looper)
        looper.setMessageLogging(looperPrinter)
    }

    fun stop() {
        if (!this::looperPrinter.isInitialized) return
        Looper.getMainLooper().setMessageLogging(null)
        looperPrinter.stop()
    }


    private class LooperPrinter(val looper: Looper) : Printer {
        private val startTag = ">"
        private val endTag = "<"

        private val date = Date()
        private var startTime: Long = 0
        private var blockThreshold = 300L
        private val mainThread = looper.thread
        private val timeFormat = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.UK)

        private val timeoutTask = Runnable {
            val stackTraceStr = StackTraceUtil.subStackTraceString(mainThread.stackTrace, 0)
            LogUtil.i(TAG, "发生卡顿 --> \n$stackTraceStr")
        }


        fun stop() {
            ThreadUtil.cancelSchedule(timeoutTask)
        }

        override fun println(x: String) {
            // 调试器连接时不监测
            if (Debug.isDebuggerConnected()) return

            // 开始处理任务
            if (x.startsWith(startTag)) {
                startTime = System.currentTimeMillis()
                ThreadUtil.schedule(timeoutTask, blockThreshold)
                return
            }

            // 结束处理任务
            ThreadUtil.cancelSchedule(timeoutTask)
            val endTime = System.currentTimeMillis()
            val spaceTime = endTime - startTime
            if (spaceTime > blockThreshold) {
                handleBlock(startTime, endTime, spaceTime, x)
            }
        }


        private fun handleBlock(startTime: Long, endTime: Long, blockTime: Long, x: String) {
            LogUtil.i(TAG, "卡顿统计 --> 开始：${getDateTime(startTime)}  结束：${getDateTime(endTime)} 卡顿时长：${blockTime}ms \nsystemLog：$x")
        }


        /**
         * 获取格式化时间
         *
         * @return
         */
        fun getDateTime(timestamp: Long): String {
            date.time = timestamp
            return timeFormat.format(date)
        }
    }
}