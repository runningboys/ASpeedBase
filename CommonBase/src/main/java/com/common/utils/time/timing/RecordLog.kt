package com.common.utils.time.timing

interface RecordLog {
    /**
     * 记录解析成日志内容
     *
     * @param data
     * @return
     */
    fun parseLog(data: RecordData): String

    /**
     * 打印日志
     *
     * @param msg
     */
    fun printLog(msg: String)
}