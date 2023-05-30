package com.common.utils.log

import android.util.Log
import java.util.Locale

/**
 * 堆栈工具
 *
 * @author LiuFeng
 * @data 2018/11/6 11:15
 */
object StackTraceUtil {
    /** 堆栈过滤源  */
    private val STACK_TRACE_ORIGIN = LogUtil::class.java.name

    /**
     * 获取堆栈信息
     *
     * @param tr
     *
     * @return
     */
    fun getStackTraceString(tr: Throwable?): String {
        return Log.getStackTraceString(tr)
    }

    /**
     * 获取堆栈信息
     *
     * @param maxDepth 小于或等于0时将全部展示
     *
     * @return
     */
    fun getStackTraceString(maxDepth: Int): String {
        val stackTrace = Thread.currentThread().stackTrace
        return subStackTraceString(stackTrace, 0, if (maxDepth <= 0) stackTrace.size else maxDepth)
    }

    /**
     * 获取裁剪后堆栈信息
     * 备注：裁剪调了过滤源前的信息
     *
     * @param maxDepth 小于或等于0时将全部展示
     *
     * @return
     */
    fun getCroppedStackTraceString(maxDepth: Int): String {
        val stackTrace = Thread.currentThread().stackTrace

        // 取忽略堆栈深度
        var ignoreDepth = 0
        val total = stackTrace.size
        for (i in 0 until total) {
            val className = stackTrace[i].className
            if (className == STACK_TRACE_ORIGIN) {
                ignoreDepth = i + 1
                break
            }
        }

        return subStackTraceString(
            stackTrace,
            ignoreDepth,
            if (maxDepth <= 0) total else ignoreDepth + maxDepth
        )
    }

    /**
     * 从指定开始位置裁取指定最大深度调用栈
     *
     * @param stackTrace
     * @param start
     * @param maxDepth
     *
     * @return
     */
    @JvmStatic
    fun getCroppedStackTraceString(stackTrace: Array<StackTraceElement>, start: Int, maxDepth: Int): String {
        val total = stackTrace.size
        return subStackTraceString(
            stackTrace,
            start,
            if (maxDepth <= 0) total else start + maxDepth
        )
    }


    /**
     * 截取堆栈信息
     *
     * @param stackTrace
     * @param start
     * @param end
     *
     * @return
     */
    @JvmOverloads
    fun subStackTraceString(
        stackTrace: Array<StackTraceElement>?,
        start: Int,
        end: Int = stackTrace?.size ?: 0
    ): String {
        var start = start
        var end = end
        if (stackTrace.isNullOrEmpty()) {
            return "stackTrace is empty"
        }

        // start和end无效值处理
        val total = stackTrace.size
        if (start < 0 || start >= total) {
            start = 0
        }
        if (end <= 0 || end > total) {
            end = total
        }

        // 拼装堆栈数据
        val builder = StringBuilder()
        for (i in start until end) {
            builder.append(getStackTraceString(stackTrace[i]))
            builder.append("\n")
        }
        return builder.toString()
    }

    /**
     * 获取堆栈信息
     *
     * @param element 堆栈数据
     *
     * @return
     */
    private fun getStackTraceString(element: StackTraceElement): String {
        val format = "at %s.%s(%s:%d)"
        val className = element.className
        val fileName = element.fileName
        val methodName = element.methodName
        val methodLine = element.lineNumber
        return String.format(Locale.CHINESE, format, className, methodName, fileName, methodLine)
    }
}