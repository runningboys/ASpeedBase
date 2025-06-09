package com.common.utils.resource

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object GzipUtil {

    /**
     * 压缩数据
     */
    fun compress(data: ByteArray): ByteArray {
        val out = ByteArrayOutputStream()
        val gzip = GZIPOutputStream(out)
        gzip.write(data)
        gzip.close()
        gzip.finish()
        return out.toByteArray()
    }


    /**
     * 解压数据
     */
    fun decompress(compressedData: ByteArray): ByteArray {
        val out = ByteArrayOutputStream()
        val inStream = ByteArrayInputStream(compressedData)
        val gunZip = GZIPInputStream(inStream)
        val buffer = ByteArray(1024)
        var bytesRead = gunZip.read(buffer)
        while (bytesRead != -1) {
            out.write(buffer, 0, bytesRead)
            bytesRead = gunZip.read(buffer)
        }
        gunZip.close()
        return out.toByteArray()
    }
}
