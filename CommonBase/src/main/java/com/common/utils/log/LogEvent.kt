package com.common.utils.log

/**
 * 日志事件
 *
 * @author LiuFeng
 * @data 2019/3/12 10:49
 */
class LogEvent(
    val tag: String? = null,
    val level: LogLevel,
    val message: String? = null,
    val timestamp: Long,
    val threadName: String,
    val stackTrace: Array<StackTraceElement>
)