package com.common.utils.log

/**
 * 打印到控制台日志格式
 *
 * @author LiuFeng
 * @data 2021/3/15 22:55
 */
class ConsoleLogFormat : LogFormat() {
    override fun format(logEvent: LogEvent, TAG: String): String {
        buffer.append("[")
        buffer.append(logEvent.level.name)
        buffer.append("] ")
        buffer.append(getDateTime(logEvent.timestamp))
        buffer.append(" ")
        buffer.append(getStackTrace(logEvent.threadName, logEvent.stackTrace, 5))
        buffer.append(" ")
        buffer.append(logEvent.tag)
        buffer.append(" ")
        buffer.append(logEvent.message)
        val content = buffer.toString()
        buffer.delete(0, buffer.length)
        return content
    }
}