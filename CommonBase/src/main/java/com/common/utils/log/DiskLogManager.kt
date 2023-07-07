package com.common.utils.log

import android.util.Log
import java.io.File
import java.util.Collections


/**
 * 磁盘日志按条件清理
 */
object DiskLogManager {
    // 上次删除文件时间戳
    private var lastDelTime: Long = 0

    // 一天的时间
    private const val ONE_DAY_TIME = 24 * 60 * 60 * 1000

    // 日志保存天数
    private const val SAVE_OF_DAYS = 15

    // 目录保存日志文件最大500Mb
    private const val SAVE_LOG_MAX_BYTES = 500 * 1024 * 1024

    /**
     * 处理文件:删除过期或过大文件
     */
    fun handleFile(folderPath: String) {
        val currentTime = System.currentTimeMillis()
        // 判断上次执行删除是否在一天以前
        if (currentTime - lastDelTime > ONE_DAY_TIME) {
            val fileList = listFilesInDir(File(folderPath))
            if (!fileList.isNullOrEmpty()) {
                try {
                    // 按修改时间降序排序
                    Collections.sort(fileList) { o1, o2 ->
                        val diff = o2.lastModified() - o1.lastModified()
                        if (diff > 0) 1 else if (diff == 0L) 0 else -1
                    }
                } catch (e: Exception) {
                    Log.e("DiskLogManager", "sort log files: ", e)
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
}