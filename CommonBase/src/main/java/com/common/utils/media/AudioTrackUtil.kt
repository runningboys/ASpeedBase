package com.common.utils.media

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Handler
import android.os.HandlerThread
import com.common.utils.thread.UIHandler
import com.common.utils.log.LogUtil
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.concurrent.LinkedBlockingQueue


object AudioTrackUtil {
    private var isInit = false
    private var bufferSizeInBytes = 0
    private const val defaultSampleRate = 24000
    private var curSampleRate = defaultSampleRate
    private var audioTrack: AudioTrack? = null
    private val bufferQueue = LinkedBlockingQueue<AudioStream>()
    private var monitor: ((label: String, audioData: ByteArray, isEnd: Boolean) -> Unit)? = null
    private val handlerThread = HandlerThread("play-audio").apply { start() }
    private val handler = Handler(handlerThread.looper)


    @JvmStatic
    fun init() {
        pause()
        release()
        val channelConfig = AudioFormat.CHANNEL_OUT_MONO // 声道配置
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT // 音频格式

        bufferSizeInBytes = AudioTrack.getMinBufferSize(curSampleRate, channelConfig, audioFormat)
        audioTrack = AudioTrack(AudioManager.STREAM_MUSIC, curSampleRate, channelConfig, audioFormat, bufferSizeInBytes, AudioTrack.MODE_STREAM)

        handler.post {
            while (true) {
                val audioStream = bufferQueue.take()
                val label = audioStream.label
                val isEnd = audioStream.isEnd
                val audioData = audioStream.data

                // 小数据块直接处理
                if (audioData.size < 6000) {
                    if (isInit) kotlin.runCatching { audioTrack?.write(audioData, 0, audioData.size) }
                    UIHandler.run { monitor?.invoke(label, audioData, isEnd) }
                    continue
                }

                // 大数据块进行拆分，以平滑处理UI声音动画
                val dataList = audioData.asList().chunked(4000) { it.toByteArray() }
                for ((index, data) in dataList.withIndex()) {
                    if (isInit) kotlin.runCatching { audioTrack?.write(data, 0, data.size) }
                    UIHandler.run { monitor?.invoke(label, data, isEnd && index == dataList.lastIndex) }
                }
            }
        }

        isInit = true
    }

    @JvmStatic
    fun isInit() = isInit


    @JvmStatic
    fun play(sampleRate: Int = defaultSampleRate) {
        curSampleRate = sampleRate
        if (!isInit()) init()
        audioTrack?.play()
    }


    @JvmStatic
    fun start() {
        audioTrack?.play()
    }


    @JvmStatic
    fun play(label: String, filePath: String, sampleRate: Int = defaultSampleRate) {
        val file = File(filePath)
        if (!file.exists() || file.length() == 0L) {
            return
        }

        try {
            release()
            play(sampleRate)

            var buffer: ByteArray
            var cursor = 0
            val fileLen = file.length()
            val inputStream = FileInputStream(file)
            while (inputStream.read(ByteArray(bufferSizeInBytes).also { buffer = it }) != -1) {
                cursor += bufferSizeInBytes
                bufferQueue.add(AudioStream(label, buffer, cursor >= fileLen))
            }

            inputStream.close()
        } catch (e: Exception) {
            LogUtil.e(e)
        }
    }


    @JvmStatic
    fun play(label: String, inputStream: InputStream, sampleRate: Int = defaultSampleRate) {
        try {
            release()
            play(sampleRate)

            var cursor = 0
            var buffer: ByteArray
            val fileLen = inputStream.available()
            while (inputStream.read(ByteArray(bufferSizeInBytes).also { buffer = it }) != -1) {
                cursor += bufferSizeInBytes
                bufferQueue.add(AudioStream(label, buffer, cursor >= fileLen))
            }

            inputStream.close()
        } catch (e: Exception) {
            LogUtil.e(e)
        }
    }


    /**
     * 写入原始音频数据
     */
    @JvmStatic
    fun write(audioStream: AudioStream) {
        bufferQueue.add(audioStream)
    }


    @JvmStatic
    fun getMonitor(monitor: (label: String, audioData: ByteArray, isEnd: Boolean) -> Unit) {
        AudioTrackUtil.monitor = monitor
    }


    @JvmStatic
    fun pause() {
        kotlin.runCatching { audioTrack?.pause() }
    }


    @JvmStatic
    fun stop() {
        kotlin.runCatching {
            bufferQueue.clear()
            audioTrack?.flush()
            audioTrack?.stop()
        }
    }


    @JvmStatic
    fun release() {
        bufferQueue.clear()
        handler.removeMessages(0)
        kotlin.runCatching { audioTrack?.release() }
        isInit = false
    }
}


interface AudioListener {
    fun onAudio(label: String, audioData: ByteArray, isEnd: Boolean)
}


class AudioStream(val label: String, val data: ByteArray, val isEnd: Boolean)