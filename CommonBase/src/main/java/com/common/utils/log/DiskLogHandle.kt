package com.common.utils.log

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * 磁盘文件日志操作器--写入文件
 *
 * @author LiuFeng
 * @date 2018-9-01
 */
class DiskLogHandle(
    val mContext: Context,
    logFormat: LogFormat,
    val mFolderPath: String
) : LogHandle(logFormat) {
    // 上次打印日志时间戳
    private var lastLogTime: Long = 0

    private val handler: Handler
    private val buffer = StringBuilder()

    init {
        val logDir = File(mFolderPath)
        if (!logDir.exists()) logDir.mkdirs()
        val thread = HandlerThread("DiskLogHandle")
        thread.start()
        handler = WriteHandler(thread.looper)
    }

    companion object {
        private const val LOG_FILE_NAME = "yyyy-MM-dd-HH" // 日志文件名格式
        private const val EMPTY_WHAT = 100 // 空消息what
        private const val LOG_WHAT = 200 // 日志消息what
        private const val SPACE_TIME: Long = 1000 // 间隔1s打印
        private const val MAX_CHAR_NUM = 500 * 1024 / 2 // 500kb的汉字数量
        private const val MAX_BYTES = 1024 * 1024 // 单个日志文件1Mb
    }

    @Synchronized
    override fun log(logEvent: LogEvent) {
        buffer.append(logFormat.format(logEvent, tag))
        handleLog()
    }

    /**
     * 处理日志数据
     */
    private fun handleLog() {
        val isMoreThanMax = buffer.length >= MAX_CHAR_NUM
        if (!handler.hasMessages(EMPTY_WHAT) || isMoreThanMax) {
            // 超过最大字符数量或距上次打印超过1s，则立即打印和发一个延时消息
            if (isMoreThanMax || System.currentTimeMillis() - lastLogTime > SPACE_TIME) {
                sendLogMessage()
                handler.sendEmptyMessageDelayed(EMPTY_WHAT, SPACE_TIME)
            } else {
                handler.sendEmptyMessageDelayed(EMPTY_WHAT, SPACE_TIME)
            }
        }
    }

    /**
     * 发送日志消息
     */
    @Synchronized
    private fun sendLogMessage() {
        val length = buffer.length
        if (length > 0) {
            handler.sendMessage(handler.obtainMessage(LOG_WHAT, buffer.toString()))

            // 日志发送后清空buffer
            buffer.delete(0, length)

            // 赋新值
            lastLogTime = System.currentTimeMillis()
        }
    }

    /**
     * 立刻刷入日志到文件
     */
    fun flushLog() {
        sendLogMessage()
    }

    /**
     * 写入日志文件处理者
     */
    internal inner class WriteHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            // 空消息
            if (msg.what == EMPTY_WHAT) {
                sendLogMessage()
                return
            }

            // 判断写入权限
            if (hasStoragePermission(mContext)) {
                val content = msg.obj as String

                // 处理文件:过期或过大
                DiskLogManager.handleFile(mFolderPath)

                // 通过时间戳生成文件名
                val fileName =
                    DateFormat.format(Companion.LOG_FILE_NAME, System.currentTimeMillis()).toString()
                val logFile = getLogFile(mFolderPath, fileName)
                // 写入日志内容到文件
                writeLogToFile(logFile, content)
            }
        }
    }

    /**
     * 权限检测
     *
     * @param permission
     *
     * @return
     */
    private fun hasStoragePermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            true
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        val perms = arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO
        )
        for (perm in perms) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }


    /**
     * 获取日志文件--文件大小超过指定大小时新建一个文件
     *
     * @param folderName
     * @param fileName
     *
     * @return
     */
    private fun getLogFile(folderName: String, fileName: String): File {
        val folder = File(folderName)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        var newFileCount = 0
        var newFile: File
        var existingFile: File? = null
        newFile = File(folder, String.format("%s.txt", fileName))
        while (newFile.exists()) {
            existingFile = newFile
            newFileCount++
            newFile = File(folder, String.format("%s(%s).txt", fileName, newFileCount))
        }
        return if (existingFile != null) {
            if (existingFile.length() >= MAX_BYTES) {
                newFile
            } else existingFile
        } else newFile
    }

    /**
     * 获取包含错误文件名的日志文件--文件大小超过指定大小时新建一个文件
     *
     * @param folderName
     * @param fileName
     * @param level
     *
     * @return
     */
    private fun getErrorLogFile(folderName: String, fileName: String, level: LogLevel): File {
        val folder = File(folderName)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        var newFileCount = 0
        val newFile: File
        var existingFile: File? = null
        val normalFormat = "%s%s.txt"
        val warnFormat = LogLevel.WARN.name + "_%s%s.txt"
        val errorFormat = LogLevel.ERROR.name + "_%s%s.txt"
        while (true) {
            // 后缀名
            val postfix = if (newFileCount == 0) "" else "($newFileCount)"

            // 普通文件
            val normalFile = File(folder, String.format(normalFormat, fileName, postfix))
            if (normalFile.exists()) {
                existingFile = normalFile
                newFileCount++
                continue
            }

            // 错误文件
            val errorFile = File(folder, String.format(errorFormat, fileName, postfix))
            if (errorFile.exists()) {
                existingFile = errorFile
                newFileCount++
                continue
            }

            // 警告文件
            val warnFile = File(folder, String.format(warnFormat, fileName, postfix))
            if (warnFile.exists()) {
                existingFile = warnFile
                newFileCount++
                continue
            }
            newFile = when (level) {
                LogLevel.ERROR -> errorFile
                LogLevel.WARN -> warnFile
                else -> normalFile
            }
            break
        }
        if (existingFile != null) {
            // 超过指定文件大小则启用新文件
            if (existingFile.length() >= MAX_BYTES) {
                return newFile
            }
            val name = existingFile.name
            var containLevel = false
            for (logLevel in LogLevel.values()) {
                // 判断文件前缀日志等级
                if (name.startsWith(logLevel.name)) {
                    containLevel = true
                    // 当前将打印日志等级大于文件原先等级
                    if (level.code > logLevel.code) {
                        val dest = File(
                            existingFile.parent + File.separator + name.replace(
                                logLevel.name,
                                level.name
                            )
                        )
                        // 修改文件前缀日志等级
                        if (existingFile.renameTo(dest)) {
                            return dest
                        }
                    }
                    break
                }
            }

            // 不包含日志等级文件添加前缀日志等级
            if (!containLevel && level.code >= LogLevel.WARN.code) {
                val dest = File(existingFile.parent + File.separator + level.name + "_" + name)
                if (existingFile.renameTo(dest)) {
                    return dest
                }
            }
            return existingFile
        }
        return newFile
    }

    /**
     * 写入日志到文件
     *
     * @param logFile
     * @param content
     */
    private fun writeLogToFile(logFile: File?, content: String) {
        if (logFile == null || TextUtils.isEmpty(content)) {
            return
        }

        // 不存在时先创建文件
        if (!logFile.exists()) {
            try {
                if (!logFile.createNewFile()) {
                    return
                }
            } catch (e: IOException) {
                Log.e(tag, "createNewFile: ", e)
                return
            }
        }
        var bw: BufferedWriter? = null
        try {
            bw = BufferedWriter(FileWriter(logFile, true))
            bw.write(content)
            bw.newLine()
            bw.flush()
        } catch (e: IOException) {
            Log.e(tag, "writeLogToFile: ", e)
        } finally {
            if (bw != null) {
                try {
                    bw.close()
                } catch (e: IOException) {
                    Log.e(tag, "writeLogToFile: ", e)
                }
            }
        }
    }
}