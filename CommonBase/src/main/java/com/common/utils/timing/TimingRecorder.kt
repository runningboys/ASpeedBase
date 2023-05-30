package com.common.utils.timing

import com.common.utils.log.LogUtil
import java.util.concurrent.ConcurrentHashMap

/**
 * 一个实用程序类，用来帮助记录在方法调用中的耗时。
 * 用法如下:
 *
 * <pre>
 * TimingRecorder.addRecord(label, "work");
 * // ... do some work A ...
 * TimingRecorder.addRecord(label, "work A");
 * // ... do some work B ...
 * TimingRecorder.addRecord(label, "work B");
 * // ... do some work C ...
 * TimingRecorder.addRecord(label, "work C");
 * TimingRecorder.logTime(label);
</pre> *
 *
 *
 * logTime调用会后输出示例如下:
 *
 * <pre>
 * 标签: label <开始>
 * 0 ms  message: 开始
 * 1501 ms  message: 执行A
 * 302 ms  message: 执行B
 * 2002 ms  message: 执行完成~
 * <结束>   总耗时: 3805 ms   记录次数: 4 次
</结束></开始></pre> *
 *
 * @author LiuFeng
 * @date 2020-10-21
 */
object TimingRecorder {
    private const val TAG = "TimingRecorder"

    // 是否调用打印
    private const val isToLog = true

    // 单个标签可以添加记录的最大数量
    private const val RECORD_MAX_NUM = 1000

    // 默认日志记录格式
    private val defaultLog: RecordLog = DefaultRecordLog()

    // 记录储存容器
    private val recordMap = ConcurrentHashMap<String, MutableList<RecordTime>>()


    /**
     * 添加记录
     *
     * @param label   记录的标签
     * @param message 记录的日志内容。
     */
    fun addRecord(label: String, message: String?) {
        // 时间记录
        val recordTime = RecordTime()
        // Android系统可以使用SystemClock.elapsedRealtime()
        // 代替System.nanoTime() / 1000_000，作用相同，这里是考虑到Java里使用
        recordTime.time = System.nanoTime() / 1000000
        recordTime.message = message

        // 处理存入记录
        handleRecordTimes(label, recordTime)
    }

    /**
     * 处理记录数据集合（数据过多时将释放）
     *
     * @param label
     * @param recordTime
     */
    @Synchronized
    private fun handleRecordTimes(label: String, recordTime: RecordTime) {
        // 取出记录
        var recordTimes = getRecordTimes(label)

        // 超过记录容量时，调用打印释放，目的避免有未打印记录占用过多内存
        if (recordTimes.size >= RECORD_MAX_NUM) {
            val desc = "$label: Add too many records! will invoke logTime."

            // 保留第一条，是为了释放打印后，新的记录中能计算到从第一条开始的耗时
            val firstRecordTime = recordTimes[0]

            // 执行打印记录，并清除标签
            logTime(label, desc)

            // 存入第一条数据到新容器
            recordTimes = getRecordTimes(label)
            recordTimes.add(firstRecordTime)
        }

        // 存入新记录
        recordTimes.add(recordTime)
    }

    /**
     * 获取标签对应容器
     *
     * @param label
     * @return
     */
    private fun getRecordTimes(label: String): MutableList<RecordTime> {
        var recordTimes = recordMap[label]

        // 创建容器
        if (recordTimes == null) {
            recordTimes = ArrayList()
            val tempRecords = recordMap.putIfAbsent(label, recordTimes)
            if (tempRecords != null) {
                recordTimes = tempRecords
            }
        }
        return recordTimes
    }


    /**
     * 打印记录时间
     *
     * @param label 记录的标签
     * @param msg   追加的日志内容。
     * @return
     */
    @JvmOverloads
    fun logTime(label: String, msg: String? = null, recordLog: RecordLog = defaultLog): String {
        val recordTimes: MutableList<RecordTime>?

        // 加锁保证添加数据和移除数据操作安全
        synchronized(TimingRecorder::class.java) {
            // 取出记录，清除标记
            recordTimes = recordMap.remove(label)
        }

        // 在记录容量内，追加一条表示结束的记录
        if (recordTimes != null && recordTimes.size < RECORD_MAX_NUM) {
            val recordTime = RecordTime()
            recordTime.time = System.nanoTime() / 1000000
            recordTime.message = msg ?: "log time end."
            recordTimes.add(recordTime)
        }

        // 解析记录
        val record = parseRecord(label, recordTimes)
        val log = recordLog.parseLog(record)

        // 打印
        if (isToLog) {
            recordLog.printLog(log)
        }
        return log
    }

    /**
     * 解析记录数据
     *
     * @param label
     * @param recordTimes
     * @return
     */
    private fun parseRecord(label: String, recordTimes: List<RecordTime>?): RecordData {
        val data = RecordData()
        data.label = label
        data.recordTimes = recordTimes

        if (!recordTimes.isNullOrEmpty()) {
            val size = recordTimes.size

            // 第一次记录时间
            val firstTime = recordTimes[0].time

            // 最后一次记录时间
            val lastTime = recordTimes[size - 1].time

            // 总耗时
            data.totalTime = lastTime - firstTime

            // 计算耗时的前一个时间
            var prevTime = firstTime

            // 取出数据计算差值并拼接
            for (record in recordTimes) {
                record.spaceTime = record.time - prevTime
                prevTime = record.time
            }
        }
        return data
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

    /**
     * 记录时间的model
     */
    class RecordTime {
        var time: Long = 0
        var spaceTime: Long = 0
        var message: String? = null
    }

    class RecordData {
        var label: String? = null
        var totalTime: Long = 0
        var recordTimes: List<RecordTime>? = null
    }

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

    class DefaultRecordLog : RecordLog {
        override fun parseLog(data: RecordData): String {
            // 组装数据
            val buffer = StringBuilder()
            buffer.append("\n标签: ").append(data.label)

            // 无记录处理
            val recordTimes = data.recordTimes
            if (recordTimes == null || recordTimes.isEmpty()) {
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

        override fun printLog(msg: String) {
            // Java里用System.out，Android最好是用Log
//        System.out.println(msg);
//        Log.println(Log.INFO, TAG, msg);
            LogUtil.d(TAG, msg)
        }
    }
}