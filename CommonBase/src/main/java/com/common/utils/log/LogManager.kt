package com.common.utils.log

import java.util.concurrent.Executors

/**
 * 日志管理器。
 *
 * @author LiuFeng
 * @data 2018/9/20 11:46
 */
object LogManager {
    /** 单线程池执行器  */
    private val executor = Executors.newSingleThreadExecutor()

    /** 日志处理器列表。  */
    private val handles = ArrayList<LogHandle>()

    /** 当前日志等级。  */
    var level = LogLevel.DEBUG

    /** 日志是否可用  */
    var isLoggable = true

    /** USB是否已连接  */
    var isUsbConnected = true

    /**
     * 构造函数。
     */
    init {
        init()
    }

    /**
     * 初始化默认日志处理
     */
    private fun init() {
        val defaultLogHandle: LogHandle = DefaultLogHandle(ConsoleLogFormat())
        handles.add(defaultLogHandle)
    }

    /**
     * 设置usb连接状态
     *
     * @param usbConnected
     */
    fun setUsbConnect(usbConnected: Boolean) {
        isUsbConnected = usbConnected
    }

    /**
     * 设置日志的tag
     *
     * @param tag
     */
    fun setLogTag(tag: String) {
        for (handle in handles) {
            handle.tag = tag
        }
    }

    /**
     * 记录日志。
     *
     * @param level   指定该日志的记录等级。
     * @param tag     指定日志标签。
     * @param message 指定日志内容。
     */
    fun log(level: LogLevel, tag: String, message: String) {
        if (filterInvalidLog(level)) {
            return
        }

        // 执行打印
        processLog(level, tag, message, Thread.currentThread().stackTrace)
    }

    /**
     * 记录日志。
     *
     * @param level      指定该日志的记录等级。
     * @param tag        指定日志标签。
     * @param message    指定日志内容。
     * @param stackTrace 调用栈
     */
    fun log(level: LogLevel, tag: String, message: String, stackTrace: Array<StackTraceElement>) {
        if (filterInvalidLog(level)) {
            return
        }

        // 执行打印
        processLog(level, tag, message, stackTrace)
    }

    /**
     * 执行打印
     *
     * @param level
     * @param tag
     * @param message
     * @param stackTrace
     */
    private fun processLog(level: LogLevel, tag: String, message: String, stackTrace: Array<StackTraceElement>) {
        // 当前线程名
        val threadName = Thread.currentThread().name

        // 所有打印放入单线程池中，避免主线程阻塞
        executor.execute {
            val logEvent = LogEvent(tag, level, message, System.currentTimeMillis(), threadName, stackTrace)
            for (handle in handles) {
                // 默认日志打印时，非连接USB状态则跳过打印（提升性能）
                if (handle is DefaultLogHandle && !isUsbConnected) {
                    continue
                }
                handle.log(logEvent)
            }
        }
    }

    /**
     * 立刻刷入日志到文件
     */
    fun flushLog() {
        if (filterInvalidLog(LogLevel.INFO)) {
            return
        }
        synchronized(this) {
            for (handle in handles) {
                // 判断是否磁盘日志操作器
                if (handle is DiskLogHandle) {
                    handle.flushLog()
                }
            }
        }
    }

    /**
     * 过滤无效日志
     *
     * @param level
     *
     * @return
     */
    private fun filterInvalidLog(level: LogLevel): Boolean {
        // 日志不可用
        return if (!isLoggable) true else level.code < this.level.code

        // 过滤较低日志等级
    }

    /**
     * 获得指定名称的处理器。
     *
     * @param name 指定处理器名称。
     *
     * @return 返回指定名称的处理器。
     */
    fun getHandle(name: String): LogHandle? {
        synchronized(this) {
            for (handle in handles) {
                if (handle.logHandleName == name) {
                    return handle
                }
            }
        }
        return null
    }

    /**
     * 添加日志内容处理器。
     *
     * @param handle 需添加的日志处理器。
     */
    fun addHandle(handle: LogHandle) {
        synchronized(this) {
            if (handles.contains(handle)) {
                return
            }
            for (h in handles) {
                if (h.logHandleName == handle.logHandleName) {
                    return
                }
            }
            handles.add(handle)
        }
    }

    /**
     * 移除日志内容处理器。
     *
     * @param handle 需移除的日志处理器。
     */
    fun removeHandle(handle: LogHandle) {
        synchronized(this) { handles.remove(handle) }
    }

    /**
     * 移除所有日志内容处理器。
     */
    fun removeAllHandles() {
        synchronized(this) { handles.clear() }
    }
}