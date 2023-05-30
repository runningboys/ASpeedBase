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
import java.util.Arrays
import java.util.Collections

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

    // 上次删除文件时间戳
    private var lastDelTime: Long = 0

    private val handler: Handler
    private val buffer = StringBuilder()

    init {
        val thread = HandlerThread("DiskLogHandle")
        thread.start()
        handler = WriteHandler(thread.looper)
    }

    companion object {
        private const val LOG_FILE_NAME = "yyyy-MM-dd-HH" // 日志文件名格式
        private const val EMPTY_WHAT = 100 // 空消息what
        private const val LOG_WHAT = 200 // 日志消息what
        private const val SPACE_TIME: Long = 1000 // 间隔1s打印
        private const val ONE_DAY_TIME = 24 * 60 * 60 * 1000 // 一天的时间
        private const val MAX_CHAR_NUM = 500 * 1024 / 2 // 500kb的汉字数量
        private const val MAX_BYTES = 1024 * 1024 // 单个日志文件1Mb
        private const val SAVE_LOG_MAX_BYTES = 100 * 1024 * 1024 // 目录保存日志文件最大100Mb
        private const val SAVE_OF_DAYS = 15 // 日志保存天数
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
            if (checkPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                val content = msg.obj as String

                // 处理文件:过期或过大
                handleFile()

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
    private fun checkPermission(context: Context?, permission: String): Boolean {
        if (context == null) {
            return false
        }
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            true
        } else ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 处理文件:删除过期或过大文件
     */
    private fun handleFile() {
        val currentTime = System.currentTimeMillis()
        // 判断上次执行删除是否在一天以前
        if (currentTime - lastDelTime > ONE_DAY_TIME) {
            val fileList = listFilesInDir(File(mFolderPath))
            if (!fileList.isNullOrEmpty()) {
                try {
                    // 按修改时间降序排序
                    Collections.sort(fileList) { o1, o2 ->
                        val diff = o2.lastModified() - o1.lastModified()
                        if (diff > 0) 1 else if (diff == 0L) 0 else -1
                    }
                } catch (e: Exception) {
                    Log.e(tag, "sort log files: ", e)
                }

                var totalLength: Long = 0
                // 过期限定时间
                val limitTime = currentTime - SAVE_OF_DAYS * ONE_DAY_TIME
                for (file in fileList) {
                    totalLength += file.length()
                    // 根据过期或文件总大小判断，执行删除
                    if (file.lastModified() < limitTime || totalLength > SAVE_LOG_MAX_BYTES) {
                        file.delete()
                    }
                }
            }

            // 保存删除时间
            lastDelTime = currentTime
        }
    }

    /**
     * 流出文件目录下所有文件
     *
     * @param dir
     *
     * @return
     */
    private fun listFilesInDir(dir: File): List<File>? {
        if (!isDir(dir)) {
            return null
        }

        val list: MutableList<File> = ArrayList()
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            list.addAll(listOf(*files))
        }
        return list
    }

    /**
     * 判断是否是目录
     *
     * @param file 文件
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    private fun isDir(file: File?): Boolean {
        return file != null && file.exists() && file.isDirectory
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