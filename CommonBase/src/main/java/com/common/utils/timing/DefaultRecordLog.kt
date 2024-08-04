package com.common.utils.timing

import com.common.utils.log.LogUtil

object DefaultRecordLog : RecordLog {
    private const val TAG = "TimingRecorder"

    override fun parseLog(data: RecordData): String {
        // 组装数据
        val buffer = StringBuilder()
        buffer.append("\n标签: ").append(data.label)

        // 无记录处理
        val recordTimes = data.recordTimes
        if (recordTimes.isNullOrEmpty()) {
            buffer.append(" <没有记录>")
            return buffer.toString()
        }
        buffer.append(" <开始>\n")

        // 取总耗时位数长度，记作最大的位长，用于空格缩进
        val maxDigitLength = data.totalTime.toString().length
        for (record in recordTimes) {
            // 当前耗时位长
            val currentDigitLength = record.spaceTime.toString().length

            // 缩进空格字符（目的是对齐易看）
            val indentSpaceStr = getSpaceStr(maxDigitLength - currentDigitLength)

            // 拼接
            buffer.append(indentSpaceStr)
            buffer.append(record.spaceTime).append(" ms")
            buffer.append("  message: ").append(record.message)
            buffer.append("\n")
        }
        buffer.append("<结束>")
        buffer.append("   总耗时: ").append(data.totalTime).append(" ms")
        buffer.append("   记录次数: ").append(recordTimes.size).append(" 次")
        return buffer.toString()
    }


    /**
     * 生成指定数量空格字符串
     *
     * @param spaces
     * @return
     */
    private fun getSpaceStr(spaces: Int): String {
        val number = (if (spaces <= 0) "" else spaces).toString()
        return String.format("%" + number + "s", "")
    }

    override fun printLog(msg: String) {
        // Java里用System.out，Android最好是用Log
//        System.out.println(msg);
//        Log.println(Log.INFO, TAG, msg);
        LogUtil.d(TAG, msg)
    }

}