package com.common.utils.time.timing

import com.common.utils.log.LogUtil

object SimpleRecordLog : RecordLog {
    override fun parseLog(data: RecordData): String {
        // 组装数据
        val buffer = StringBuilder()
        buffer.append("标签: ").append(data.label)

        // 无记录处理
        val recordTimes = data.recordTimes
        if (recordTimes.isNullOrEmpty()) {
            buffer.append(" <没有记录>")
            return buffer.toString()
        }
        buffer.append("   总耗时: ").append(data.totalTime).append(" ms")
        return buffer.toString()
    }

    override fun printLog(msg: String) {
       LogUtil.d(msg)
    }
}