package com.common.utils.log

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 日志格式
 *
 * @author LiuFeng
 * @data 2021/3/13 8:37
 */
abstract class LogFormat {
    protected val buffer = StringBuilder()
    private val timeFormat = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.UK)
    private val date = Date()

    /**
     * 格式化输出
     *
     * @param logEvent
     * @param TAG
     * @return
     */
    abstract fun format(logEvent: LogEvent, TAG: String): String

    /**
     * 获取格式化时间
     *
     * @return
     */
    fun getDateTime(timestamp: Long): String {
        date.time = timestamp
        return timeFormat.format(date)
    }

    /**
     * 获取堆栈信息
     *
     * @param currentThread 当前线程
     * @param stackTraceArr 堆栈数组数据
     * @param deep          取堆栈数据深度(下标)
     *
     * @return
     */
    fun getStackTrace(currentThread: String, stackTraceArr: Array<StackTraceElement>, deep: Int): String {
        if (deep >= stackTraceArr.size) {
            return "Index Out of Bounds! deep:" + deep + " length:" + stackTraceArr.size
        }

        val stackTrace = stackTraceArr[deep]
        val format = "[(%s:%d)# %s -> %s]"
        val fileName = stackTrace.fileName
        val methodLine = stackTrace.lineNumber
        val methodName = stackTrace.methodName
        return String.format(Locale.CHINESE, format, fileName, methodLine, methodName, currentThread)
    }
}