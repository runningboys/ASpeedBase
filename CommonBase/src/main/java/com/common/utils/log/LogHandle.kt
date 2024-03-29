package com.common.utils.log

/**
 * 日志操作器基类
 *
 * @author LiuFeng
 * @data 2018/9/20 11:45
 */
abstract class LogHandle(val logFormat: LogFormat) {
    var tag = "DEFAULT_TAG"

    /**
     * 获得日志句柄名称（默认类名）
     *
     * @return 返回日志句柄名称。
     */
    val logHandleName: String
        get() = this.javaClass.name

    /**
     * 日志打印
     *
     * @param logEvent 日志事件
     */
    abstract fun log(logEvent: LogEvent)
}