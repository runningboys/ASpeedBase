package com.common.utils.network.bandwidth

import android.net.TrafficStats
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import java.util.concurrent.atomic.AtomicInteger

/**
 * 带宽采集器
 * 描述：周期性地从流量中读取数据分析带宽，没有完全的上下行传输时分析不可用。
 */
object BandwidthSampler {
    private val mSamplingCounter = AtomicInteger()
    private var sPreviousBytes: Long = -1
    private var mLastTimeReading: Long = 0

    private val mHandler = HandlerThread("ParseThread")
            .apply { start() }.run { SamplingHandler(this.looper) }


    /**
     * Method call to start sampling for download bandwidth.
     */
    fun startSampling() {
        if (mSamplingCounter.getAndIncrement() == 0) {
            mHandler.startSamplingThread()
            mLastTimeReading = SystemClock.elapsedRealtime()
        }
    }

    /**
     * Finish sampling and prevent further changes to the
     * ConnectionClass until another timer is started.
     */
    fun stopSampling() {
        if (mSamplingCounter.decrementAndGet() == 0) {
            mHandler.stopSamplingThread()
            addFinalSample()
        }
    }

    /**
     * 轮询自上次更新以来的总字节数变化
     * 并将其添加到带宽管理器
     */
    private fun addSample() {
        // 获取手机开机后数据下载总量
        val newBytes = TrafficStats.getTotalRxBytes()
        // 用总下载量减去上一次计算的总下载量，就得到了在循环间隔内下载的数据量
        val byteDiff = newBytes - sPreviousBytes
        // 如果是第一次，不进行计算
        if (sPreviousBytes >= 0) {
            synchronized(this) {
                // 获取当前的时间戳
                val curTimeReading = SystemClock.elapsedRealtime()
                // 还记得之前的startSampling获取的相对时间戳吗，这里得到时间的差值
                ConnectionManager.addBandwidth(byteDiff, curTimeReading - mLastTimeReading)
                // 更新相对的时间戳
                mLastTimeReading = curTimeReading
            }
        }
        // 更新上一次的总下载量
        sPreviousBytes = newBytes
    }

    /**
     * Resets previously read byte count after recording a sample, so that
     * we don't count bytes downloaded in between sampling sessions.
     */
    private fun addFinalSample() {
        addSample()
        sPreviousBytes = -1
    }

    fun isSampling(): Boolean {
        return mSamplingCounter.get() != 0
    }

    private class SamplingHandler(looper: Looper?) : Handler(looper!!) {
        /**
         * Time between polls in ms.
         */
        private val SAMPLE_TIME: Long = 1000
        private val MSG_START = 1

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_START -> {
                    // 把样本添加进来计算网速
                    addSample()
                    // 循环获取样本计算网速
                    sendEmptyMessageDelayed(MSG_START, SAMPLE_TIME)
                }

                else -> throw IllegalArgumentException("Unknown what=" + msg.what)
            }
        }

        /**
         * 开启网络监听的循环
         */
        fun startSamplingThread() {
            sendEmptyMessage(MSG_START)
        }

        /**
         * 停止网络监听的循环
         */
        fun stopSamplingThread() {
            removeMessages(MSG_START)
        }
    }
}