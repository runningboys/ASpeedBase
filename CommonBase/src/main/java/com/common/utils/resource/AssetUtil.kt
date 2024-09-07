package com.common.utils.resource

import android.text.TextUtils
import com.common.base.BaseApp
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

/**
 * asset资源工具
 *
 * @author LiuFeng
 * @data 2021/9/18 18:27
 */
object AssetUtil {
    private val mAssetManager = BaseApp.context.assets

    /**
     * 获取资源的文件内容（例如在assets/assets.lst文件中）
     *
     * @param asset
     * @return
     */
    fun getStringFromAssets(asset: String): String {
        val sb = StringBuilder()
        try {
            val source = getInputStream(asset)
            val br = BufferedReader(InputStreamReader(source))
            var line: String?
            while (null != br.readLine().also { line = it }) {
                sb.append(line)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    /**
     * 将asset文件拷贝到指定文件夹
     *
     * @param asset
     * @param dir
     * @return
     */
    fun copyFile(asset: String, dir: File): File {
        checkFolderExists(dir)
        val fileName = getAssetFileName(asset)
        val destFile = File(dir, fileName)
        try {
            val source = getInputStream(asset)
            writeFile(source, destFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return destFile
    }

    /**
     * 复制文件夹
     *
     * @param assetsDir
     * @param dir
     */
    fun copyDir(assetsDir: String, dir: File) {
        var assetsDir = assetsDir
        checkFolderExists(dir)
        assetsDir = getAssetDirName(assetsDir)
        try {
            // 获取资源目录下文件名称列表
            val fileNames = mAssetManager.list(assetsDir)

            // 文件
            if (fileNames!!.size == 0) {
                copyFile(assetsDir, dir)
                return
            }

            // 文件夹
            for (name in fileNames) {
                //补全assets资源路径
                val filePath = assetsDir + File.separator + name
                val childNames = mAssetManager.list(filePath)
                if (!childNames.isNullOrEmpty()) {
                    copyDir(name, dir)
                } else {
                    copyFile(filePath, dir)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 将流写入目标文件
     *
     * @param source
     * @param destFile
     * @return
     */
    private fun writeFile(source: InputStream, destFile: File): Boolean {
        var source: InputStream? = source
        var bRet = true
        try {
            var read: Int
            val buffer = ByteArray(1024)
            var dest: OutputStream? = FileOutputStream(destFile)
            while (source!!.read(buffer).also { read = it } != -1) {
                dest!!.write(buffer, 0, read)
            }
            source.close()
            source = null
            dest!!.flush()
            dest.close()
            dest = null
        } catch (e: Exception) {
            e.printStackTrace()
            bRet = false
        }
        return bRet
    }

    private fun getAssetFileName(asset: String): String {
        var fileName = asset
        if (asset.contains("/")) {
            fileName = asset.substring(asset.lastIndexOf("/") + 1)
        }
        return fileName
    }

    private fun getAssetDirName(asset: String): String {
        var asset = asset
        if (TextUtils.isEmpty(asset) || asset == "/") {
            asset = ""
        }
        if (asset.endsWith("/")) {
            asset = asset.substring(0, asset.length - 1)
        }
        return asset
    }

    private fun getInputStream(asset: String): InputStream {
        return mAssetManager.open(File(asset).path)
    }

    private fun checkFolderExists(folder: File) {
        if (!folder.exists()) {
            folder.mkdirs()
        }
    }
}