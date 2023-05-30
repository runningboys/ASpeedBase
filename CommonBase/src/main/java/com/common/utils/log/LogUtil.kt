package com.common.utils.log

import android.content.Context
import android.text.TextUtils
import com.common.utils.log.StackTraceUtil.getCroppedStackTraceString
import com.common.utils.log.StackTraceUtil.getStackTraceString
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * 日志工具类
 *
 * @author LiuFeng
 * @data 2018/9/20 11:46
 */
object LogUtil {

    /**
     * 打印 DEBUG 级别日志。
     *
     * @param msg 日志内容。
     */
    @JvmStatic
    fun d(msg: String) {
        commonLog(LogLevel.DEBUG, "", msg)
    }

    /**
     * 打印 DEBUG 级别日志。
     *
     * @param tag 该条日志的标签。
     * @param msg 日志内容。
     */
    @JvmStatic
    fun d(tag: String, msg: String) {
        commonLog(LogLevel.DEBUG, tag, msg)
    }

    /**
     * 打印 INFO 级别日志。
     *
     * @param msg 日志内容。
     */
    @JvmStatic
    fun i(msg: String) {
        commonLog(LogLevel.INFO, "", msg)
    }

    /**
     * 打印 INFO 级别日志。
     *
     * @param tag 该条日志的标签。
     * @param msg 日志内容。
     */
    @JvmStatic
    fun i(tag: String, msg: String) {
        commonLog(LogLevel.INFO, tag, msg)
    }

    /**
     * 打印 INFO 级别日志和其堆栈深度信息
     *
     * @param msg      日志内容。
     * @param maxDepth 堆栈深度
     */
    @JvmStatic
    fun i(msg: String, maxDepth: Int) {
        commonLog(LogLevel.INFO, "", """$msg${getCroppedStackTraceString(maxDepth)}""".trimIndent())
    }

    /**
     * 打印 INFO 级别日志和其堆栈深度信息
     *
     * @param tag      该条日志的标签。
     * @param msg      日志内容。
     * @param maxDepth 堆栈深度
     */
    @JvmStatic
    fun i(tag: String, msg: String, maxDepth: Int) {
        commonLog(LogLevel.INFO, tag, """$msg${getCroppedStackTraceString(maxDepth)}""".trimIndent())
    }

    /**
     * 打印 WARNING 级别日志。
     *
     * @param msg 日志内容。
     */
    @JvmStatic
    fun w(msg: String) {
        commonLog(LogLevel.WARN, "", msg)
    }

    /**
     * 打印 WARNING 级别日志。
     *
     * @param tag 该条日志的标签。
     * @param msg 日志内容。
     */
    @JvmStatic
    fun w(tag: String, msg: String) {
        commonLog(LogLevel.WARN, tag, msg)
    }

    /**
     * 打印 WARNING 级别日志。
     *
     * @param throwable 日志包含的异常。
     */
    @JvmStatic
    fun w(throwable: Throwable) {
        commonLog(LogLevel.WARN, "", getExceptionLog("", throwable))
    }

    /**
     * 打印 WARNING 级别日志。
     *
     * @param tag       该条日志的标签。
     * @param throwable 日志包含的异常。
     */
    @JvmStatic
    fun w(tag: String, throwable: Throwable) {
        commonLog(LogLevel.WARN, tag, getExceptionLog("", throwable))
    }

    /**
     * 打印 ERROR 级别日志。
     *
     * @param msg 日志内容。
     */
    @JvmStatic
    fun e(msg: String) {
        commonLog(LogLevel.ERROR, "", msg)
    }

    /**
     * 打印 ERROR 级别日志。
     *
     * @param tag 该条日志的标签。
     * @param msg 日志内容。
     */
    @JvmStatic
    fun e(tag: String, msg: String) {
        commonLog(LogLevel.ERROR, tag, msg)
    }

    /**
     * 打印 ERROR 级别日志。
     *
     * @param tag
     * @param msg
     * @param stackTrace
     * @param start
     * @param maxDepth
     */
    @JvmStatic
    fun e(tag: String, msg: String, stackTrace: Array<StackTraceElement>, start: Int, maxDepth: Int) {
        commonLog(LogLevel.ERROR, tag, """$msg${getCroppedStackTraceString(stackTrace, start, maxDepth)}""".trimIndent())
    }

    /**
     * 打印 ERROR 级别日志和其堆栈深度信息
     *
     * @param tag      该条日志的标签。
     * @param msg      日志内容。
     * @param maxDepth 堆栈深度
     */
    @JvmStatic
    fun e(tag: String, msg: String, maxDepth: Int) {
        commonLog(LogLevel.ERROR, tag, """$msg${getCroppedStackTraceString(maxDepth)}""".trimIndent())
    }

    /**
     * 打印 ERROR 级别日志。
     *
     * @param throwable
     */
    @JvmStatic
    fun e(throwable: Throwable) {
        commonLog(LogLevel.ERROR, "", getExceptionLog("", throwable))
    }

    /**
     * 打印 ERROR 级别日志。
     *
     * @param throwable
     */
    @JvmStatic
    fun e(tag: String, throwable: Throwable) {
        commonLog(LogLevel.ERROR, tag, getExceptionLog("", throwable))
    }

    /**
     * 打印 ERROR 级别日志。
     *
     * @param tag
     * @param msg
     * @param throwable
     */
    @JvmStatic
    fun e(tag: String, msg: String, throwable: Throwable) {
        commonLog(LogLevel.ERROR, tag, getExceptionLog(msg, throwable))
    }

    /**
     * 打印格式化json信息
     *
     * @param json
     */
    @JvmStatic
    fun json(json: String) {
        commonLog(LogLevel.INFO, "", getFormatJson(json))
    }

    /**
     * 打印格式化json信息
     *
     * @param jsonObject
     */
    @JvmStatic
    fun json(jsonObject: JSONObject?) {
        if (jsonObject == null) {
            commonLog(LogLevel.INFO, "", "Empty/Null JSONObject")
            return
        }
        commonLog(LogLevel.INFO, "", getFormatJson(jsonObject.toString()))
    }

    /**
     * 打印格式化json信息
     *
     * @param tag
     * @param json
     */
    @JvmStatic
    fun json(tag: String, json: String) {
        commonLog(LogLevel.INFO, tag, getFormatJson(json))
    }

    /**
     * 打印格式化json信息
     *
     * @param tag
     * @param jsonObject
     */
    @JvmStatic
    fun json(tag: String, jsonObject: JSONObject?) {
        if (jsonObject == null) {
            commonLog(LogLevel.INFO, tag, "Empty/Null JSONObject")
            return
        }
        commonLog(LogLevel.INFO, tag, getFormatJson(jsonObject.toString()))
    }

    /**
     * 打印格式化json信息
     *
     * @param jsonObject
     */
    @JvmStatic
    fun json(level: LogLevel, tag: String, jsonObject: JSONObject?) {
        if (jsonObject == null) {
            commonLog(level, tag, getFormatJson("Empty/Null JSONObject"))
            return
        }
        commonLog(level, tag, getFormatJson(jsonObject.toString()))
    }

    /**
     * 打印格式化json信息
     *
     * @param level
     * @param tag
     * @param json
     */
    @JvmStatic
    fun json(level: LogLevel, tag: String, json: String) {
        commonLog(level, tag, getFormatJson(json))
    }

    /**
     * 获取格式化json
     *
     * @param jsonObject
     *
     * @return
     */
    @JvmStatic
    fun getFormatJson(jsonObject: JSONObject?): String {
        return if (jsonObject == null) {
            "Empty/Null JSONObject"
        } else getFormatJson(jsonObject.toString())
    }

    /**
     * 获取格式化json
     *
     * @param json
     *
     * @return
     */
    @JvmStatic
    fun getFormatJson(json: String): String {
        if (TextUtils.isEmpty(json)) {
            return "Empty/Null json"
        }

        var content = json.trim { it <= ' ' }
        var exception: JSONException? = null
        try {
            val obj = JSONTokener(content).nextValue()
            content = if (obj is JSONObject) {
                obj.toString(2)
            } else if (obj is JSONArray) {
                obj.toString(2)
            } else {
                "Invalid Json: $json"
            }
        } catch (e: JSONException) {
            exception = e
        }

        if (exception != null) {
            commonLog(LogLevel.ERROR, "", getExceptionLog(json, exception))
        }
        return content
    }

    /**
     * 打印格式化xml信息
     *
     * @param xml
     */
    @JvmStatic
    fun xml(xml: String) {
        commonLog(LogLevel.INFO, "", getFormatXml("", xml))
    }

    /**
     * 打印格式化xml信息
     *
     * @param tag
     * @param xml
     */
    @JvmStatic
    fun xml(tag: String, xml: String) {
        commonLog(LogLevel.INFO, tag, getFormatXml(tag, xml))
    }

    /**
     * 打印格式化xml信息
     *
     * @param level
     * @param tag
     * @param xml
     */
    @JvmStatic
    fun xml(level: LogLevel, tag: String, xml: String) {
        commonLog(level, tag, getFormatXml(tag, xml))
    }

    /**
     * 获取格式化xml
     *
     * @param tag
     * @param xml
     *
     * @return
     */
    @JvmStatic
    fun getFormatXml(tag: String, xml: String): String {
        if (TextUtils.isEmpty(xml)) {
            return "Empty/Null xml content"
        }

        var content = xml.trim { it <= ' ' }
        var exception: TransformerException? = null
        try {
            val xmlInput: Source = StreamSource(StringReader(content))
            val xmlOutput = StreamResult(StringWriter())
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(xmlInput, xmlOutput)
            content = xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n")
        } catch (e: TransformerException) {
            exception = e
        }
        if (exception != null) {
            commonLog(LogLevel.ERROR, tag, getExceptionLog(xml, exception))
        }
        return content
    }

    /**
     * 获取异常信息的log
     *
     * @param msg
     * @param throwable
     *
     * @return
     */
    private fun getExceptionLog(msg: String, throwable: Throwable): String {
        return """$msg Caught exception: ${getStackTraceString(throwable)}""".trimIndent()
    }

    /**
     * 公用日志打印方法
     *
     * @param level
     * @param tag
     * @param msg
     */
    private fun commonLog(level: LogLevel, tag: String, msg: String) {
        LogManager.log(level, tag, msg)
    }

    /**
     * 公用日志打印方法
     *
     * @param level
     * @param tag
     * @param msg
     * @param stackTrace 调用栈
     */
    private fun commonLog(level: LogLevel, tag: String, msg: String, stackTrace: Array<StackTraceElement>) {
        LogManager.log(level, tag, msg, stackTrace)
    }

    /**
     * 日志是否可用
     *
     * @return
     */
    var isLoggable: Boolean
        get() = LogManager.isLoggable
        set(loggable) {
            LogManager.isLoggable = loggable
        }

    /**
     * 设置usb连接状态
     *
     * @param usbConnected
     */
    @JvmStatic
    fun setUsbConnect(usbConnected: Boolean) {
        LogManager.setUsbConnect(usbConnected)
    }

    /**
     * 设置全局tag
     *
     * @param tag
     */
    @JvmStatic
    fun setLogTag(tag: String) {
        LogManager.setLogTag(tag)
    }

    /**
     * 添加普通日志
     */
    @JvmStatic
    fun addCommonLogHandle() {
        val defaultLogHandle: LogHandle = DefaultLogHandle(ConsoleLogFormat())
        LogManager.addHandle(defaultLogHandle)
    }

    /**
     * 添加文件日志
     */
    @JvmStatic
    fun addDiskLogHandle(context: Context, folderPath: String) {
        val diskLogHandle: LogHandle = DiskLogHandle(context, DiskLogFormat(), folderPath)
        LogManager.addHandle(diskLogHandle)
    }

    /**
     * 删除所有日志处理
     */
    @JvmStatic
    fun removeAllHandles() {
        LogManager.removeAllHandles()
    }

    /**
     * 立刻刷入日志到文件
     */
    @JvmStatic
    fun flushLog() {
        LogManager.flushLog()
    }
}