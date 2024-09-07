package com.common.utils.media

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.common.utils.ui.ToastUtil
import com.common.utils.log.LogUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


object MediaPlayerUtil : MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private var mediaPlayer: MediaPlayer? = null

    @JvmStatic
    fun init() {
        release()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        )
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnErrorListener(this)
    }

    @JvmStatic
    fun isInit() = mediaPlayer != null


    @JvmStatic
    fun play(context: Context, path: String, listener: MediaPlayListener? = null) {
        try {
            if (!isInit())
                init()
            else
                stop()
            mediaPlayer?.reset()
            mediaPlayer?.setOnPreparedListener { listener?.onPrepared() }
            mediaPlayer?.setOnCompletionListener { listener?.onCompletion() }
            mediaPlayer?.setDataSource(context, Uri.parse(path));
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        } catch (e: Exception) {
            LogUtil.e(e)
            MainScope().launch { ToastUtil.showToast("无法播放音频") }
        }
    }


    @JvmStatic
    fun playAsync(context: Context, path: String) {
        try {
            if (!isInit()) init()
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(context, Uri.parse(path));
            mediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            LogUtil.e(e)
            MainScope().launch { ToastUtil.showToast("无法播放音频") }
        }
    }

    @JvmStatic
    fun isPlaying() = mediaPlayer?.isPlaying ?: false


    @JvmStatic
    fun play(fd: AssetFileDescriptor) {
        try {
            if (!isInit()) init()
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length);
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        } catch (e: Exception) {
            LogUtil.e(e)
            MainScope().launch { ToastUtil.showToast("无法播放音频") }
        }
    }


    @JvmStatic
    fun pause() {
        try {
            mediaPlayer?.pause()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    @JvmStatic
    fun start() {
        mediaPlayer?.start()
    }

    fun setLooping(which: Boolean) {
        mediaPlayer?.isLooping = which
    }

    @JvmStatic
    fun stop() {
        mediaPlayer?.let {
            try {
                mediaPlayer!!.seekTo(mediaPlayer!!.duration)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }


    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer?.start()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        ToastUtil.showToast("音频播放错误")
        return false
    }
}


interface MediaPlayListener {
    fun onPrepared()

    fun onCompletion()
}