package com.common.utils.media

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import com.common.utils.thread.ThreadUtil
import java.util.concurrent.LinkedBlockingQueue


/**
 * 音频麦克风和扬声器测试
 *
 * @author LiuFeng
 * @data 2025/3/14 10:41
 */
object AudioTestTool {
    @Volatile
    private var isRecording = false
    @Volatile
    private var isPlaying = false

    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private val bufferQueue = LinkedBlockingQueue<ByteArray>()


    // 开始录音
    fun startRecording(context: Context) {
        stop()
        micLoopBack(context)
        startPlaying()
    }

    // 开始播放录音
    private fun startPlaying(delayMillis: Long = 2000) {
        ThreadUtil.schedule({
            if (!isRecording) return@schedule
            val track = audioTrack ?: return@schedule
            isPlaying = true

            while(isPlaying && bufferQueue.isNotEmpty()) {
                val buffer = bufferQueue.poll()
                track.write(buffer, 0, buffer.size)
            }
        }, delayMillis)
    }


    @SuppressLint("MissingPermission")
    private fun micLoopBack(context: Context) = ThreadUtil.request {
        isRecording = true
        //打开设备扬声器
        val service = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        service.setSpeakerphoneOn(true);

        val SAMPLE_RATE = 8000;
        val BUF_SIZE = 1024;

        //计算缓冲区尺寸
        var bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        bufferSize = bufferSize.coerceAtLeast(AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT));
        bufferSize = bufferSize.coerceAtLeast(BUF_SIZE);
        val buffer = ByteArray(bufferSize)

        //创建音频采集设备，输入源是麦克风
        val audioRecord = AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        // 回音消除
        if (AcousticEchoCanceler.isAvailable()) {
            AcousticEchoCanceler.create(audioRecord.audioSessionId);
        }

        //一边采集，一边播放
        audioRecord.startRecording()
        AudioTestTool.audioRecord = audioRecord


        //创建音频播放设备
        val audioTrack = AudioTrack(AudioManager.STREAM_VOICE_CALL,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
            AudioTrack.MODE_STREAM);

        audioTrack.setPlaybackRate(SAMPLE_RATE);
        audioTrack.play()
        AudioTestTool.audioTrack = audioTrack

        //需要停止的时候，把mRunning置为false即可。
        while(isRecording) {
            val readSize = audioRecord.read(buffer, 0, bufferSize)
            if (readSize > 0) bufferQueue.offer(buffer.clone())
        }
    }




    // 停止录音和播放
    fun stop() {
        isRecording = false
        isPlaying = false
        audioRecord?.release()
        audioRecord = null
        audioTrack?.release()
        audioTrack = null
    }
}