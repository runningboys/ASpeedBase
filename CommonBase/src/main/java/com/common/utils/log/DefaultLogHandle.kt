package com.common.utils.log

import android.util.Log
import java.util.Arrays

/**
 * 默认日志处理器--打印到控制台
 *
 * @author LiuFeng
 * @data 2018/9/20 11:43
 */
class DefaultLogHandle(logFormat: LogFormat) : LogHandle(logFormat) {

    override fun log(logEvent: LogEvent) {
        print(logEvent.level.code, tag, logFormat.format(logEvent, tag))
    }

    /**
     * 打印日志到控制台（解决Android控制台丢失长日志记录）
     *
     * @param priority
     * @param tag
     * @param content
     */
    private fun print(priority: Int, tag: String, content: String) {
        // 1. 测试控制台最多打印4062个字节，不同情况稍有出入（注意：这里是字节，不是字符！！）
        // 2. 字符串默认字符集编码是utf-8，它是变长编码一个字符用1~4个字节表示
        // 3. 这里字符长度小于1000，即字节长度小于4000，则直接打印，避免执行后续流程，提高性能哈
        if (content.length < 1000) {
            Log.println(priority, tag, content)
            return
        }

        // 一次打印的最大字节数
        val maxByteNum = 4000

        // 字符串转字节数组
        var bytes = content.toByteArray()

        // 超出范围直接打印
        if (maxByteNum >= bytes.size) {
            Log.println(priority, tag, content)
            return
        }

        // 分段打印计数
        var count = 1

        // 在数组范围内，则循环分段
        while (maxByteNum < bytes.size) {
            // 按字节长度截取字符串
            val subStr = cutStr(bytes, maxByteNum)

            // 打印日志
            val desc = String.format("分段打印(%s):%s", count++, subStr)
            Log.println(priority, tag, desc)

            // 截取出尚未打印字节数组
            bytes = bytes.copyOfRange(subStr.toByteArray().size, bytes.size)

            // 可根据需求添加一个次数限制，避免有超长日志一直打印
            if (count == 10) {
                break
            }
        }

        // 打印剩余部分
        Log.println(priority, tag, String.format("分段打印(%s):%s", count, String(bytes)))
    }

    /**
     * 按字节长度截取字节数组为字符串
     *
     * @param bytes
     * @param subLength
     * @return
     */
    private fun cutStr(bytes: ByteArray?, subLength: Int): String {
        // 边界判断
        if (bytes == null || subLength < 1) {
            return ""
        }

        // 超出范围直接返回
        if (subLength >= bytes.size) {
            return String(bytes)
        }

        // 复制出定长字节数组，转为字符串
        val subStr = String(Arrays.copyOf(bytes, subLength))

        // 避免末尾字符是被拆分的，这里减1使字符串保持完整
        return subStr.substring(0, subStr.length - 1)
    }
}