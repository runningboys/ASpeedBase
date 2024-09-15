/*
 *  Copyright (c) 2015, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */
package com.common.utils.network.bandwidth

import java.util.concurrent.atomic.AtomicReference

/**
 * 用于计算用户连接的近似带宽
 */
object ConnectionManager {
    private const val DEFAULT_SAMPLES_TO_QUALITY_CHANGE = 5.0

    /** 字节比特  */
    private const val BYTES_TO_BITS = 8

    /** 确定数据连接质量的默认值。带宽数字以千比特每秒(kbps)为单位  */
    private const val DEFAULT_POOR_BANDWIDTH = 150
    private const val DEFAULT_MODERATE_BANDWIDTH = 550
    private const val DEFAULT_GOOD_BANDWIDTH = 2000
    private const val DEFAULT_HYSTERESIS_PERCENT: Long = 20
    private const val HYSTERESIS_TOP_MULTIPLIER = 100.0 / (100.0 - DEFAULT_HYSTERESIS_PERCENT)
    private const val HYSTERESIS_BOTTOM_MULTIPLIER = (100.0 - DEFAULT_HYSTERESIS_PERCENT) / 100.0

    /**
     * 衰变常量
     * 用于计算当前带宽的因素，取决于先前计算的带宽值。
     * 这个值越小，新样本对移动平均线造成的影响就越小。
     */
    private const val DEFAULT_DECAY_CONSTANT = 0.05

    /** 以比特/毫秒为单位的测量带宽的下界。低于这个值的读数被视为有效的零(因此被忽略)  */
    private const val BANDWIDTH_LOWER_BOUND: Long = 10

    /** Current bandwidth of the user's connection depending upon the response.  */
    private val mDownloadBandwidth = ExponentialGeometricAverage(DEFAULT_DECAY_CONSTANT)

    @Volatile
    private var mInitiateStateChange = false
    private val mCurrentBandwidthConnectionQuality = AtomicReference(ConnectionQuality.UNKNOWN)
    private var mNextBandwidthConnectionQuality: AtomicReference<ConnectionQuality>? = null
    private val mListenerList = ArrayList<BandwidthStateListener>()
    private var mSampleCounter = 0


    /**
     * 将带宽添加到当前过滤的延迟计数器中。
     * 发送一个广播给所有[BandwidthStateListener]，
     * 如果计数器从一个桶移动到另一个桶(例：低带宽->中等带宽)。
     */
    @Synchronized
    fun addBandwidth(bytes: Long, timeInMs: Long) {
        // 忽视垃圾值。
        if (timeInMs == 0L || bytes * 1.0 / timeInMs * BYTES_TO_BITS < BANDWIDTH_LOWER_BOUND) {
            return
        }

        // 先把下载量的单位转换成比特
        val bandwidth = bytes * 1.0 / timeInMs * BYTES_TO_BITS
        // 开始测量网速
        mDownloadBandwidth.addMeasurement(bandwidth)
        // 是否初始值已经发生了变化，否则不去相应我们绑定的listener
        if (mInitiateStateChange) {
            // 样本采集数+1
            mSampleCounter += 1
            // 如果计算出来的网速与上一个网速不同
            if (currentBandwidthQuality !== mNextBandwidthConnectionQuality!!.get()) {
                // 重新开始计算网速
                mInitiateStateChange = false
                mSampleCounter = 1
            }
            // 如果计算数已经大于标准计算次数 且 与 记录的网速的峰值和最低值对比，如果大于峰值，或者小于最低值，说明网络已经发生变化
            if (mSampleCounter >= DEFAULT_SAMPLES_TO_QUALITY_CHANGE && significantlyOutsideCurrentBand()) {
                // 重新开始计算网速
                mInitiateStateChange = false
                mSampleCounter = 1
                // 记录新的网速
                mCurrentBandwidthConnectionQuality.set(mNextBandwidthConnectionQuality!!.get())
                // 回调所有的监听listener
                notifyListeners()
            }
            return
        }
        // 如果现在的网速与计算出来的网速不同
        if (mCurrentBandwidthConnectionQuality.get() !== currentBandwidthQuality) {
            // 初始值已经发生了变化
            mInitiateStateChange = true
            // 记录新的网速
            mNextBandwidthConnectionQuality = AtomicReference(currentBandwidthQuality)
        }
    }

    private fun significantlyOutsideCurrentBand(): Boolean {
        val currentQuality = mCurrentBandwidthConnectionQuality.get()
        val bottomOfBand: Double
        val topOfBand: Double
        when (currentQuality) {
            ConnectionQuality.POOR -> {
                bottomOfBand = 0.0
                topOfBand = DEFAULT_POOR_BANDWIDTH.toDouble()
            }

            ConnectionQuality.MODERATE -> {
                bottomOfBand = DEFAULT_POOR_BANDWIDTH.toDouble()
                topOfBand = DEFAULT_MODERATE_BANDWIDTH.toDouble()
            }

            ConnectionQuality.GOOD -> {
                bottomOfBand = DEFAULT_MODERATE_BANDWIDTH.toDouble()
                topOfBand = DEFAULT_GOOD_BANDWIDTH.toDouble()
            }

            ConnectionQuality.EXCELLENT -> {
                bottomOfBand = DEFAULT_GOOD_BANDWIDTH.toDouble()
                topOfBand = Float.MAX_VALUE.toDouble()
            }

            else -> return true
        }
        val average = mDownloadBandwidth.average
        if (average > topOfBand) {
            if (average > topOfBand * HYSTERESIS_TOP_MULTIPLIER) {
                return true
            }
        } else if (average < bottomOfBand * HYSTERESIS_BOTTOM_MULTIPLIER) {
            return true
        }
        return false
    }

    /**
     * Resets the bandwidth average for this instance of the bandwidth manager.
     */
    fun reset() {
        mDownloadBandwidth.reset()
        mCurrentBandwidthConnectionQuality.set(ConnectionQuality.UNKNOWN)
    }

    @get:Synchronized
    val currentBandwidthQuality: ConnectionQuality
        /**
         * 获得当前移动带宽平均值所表示的连接质量。
         *
         * @return 表示此时设备带宽的连接质量。
         */
        get() {
            val average = mDownloadBandwidth.average
            if (average < 0) {
                return ConnectionQuality.UNKNOWN
            }
            if (average < DEFAULT_POOR_BANDWIDTH) {
                return ConnectionQuality.POOR
            }
            if (average < DEFAULT_MODERATE_BANDWIDTH) {
                return ConnectionQuality.MODERATE
            }
            return if (average < DEFAULT_GOOD_BANDWIDTH) {
                ConnectionQuality.GOOD
            } else ConnectionQuality.EXCELLENT
        }


    /**
     * Accessor method for the current bandwidth average.
     *
     * @return The current bandwidth average, or -1 if no average has been recorded.
     */
    @Synchronized
    fun downloadKBitsPerSecond(): Double {
        return mDownloadBandwidth.average
    }

    /**
     * Interface for listening to when [ConnectionManager]
     * changes state.
     */
    interface BandwidthStateListener {
        /**
         * The method that will be called when [ConnectionManager]
         * changes ConnectionClass.
         *
         * @param bandwidthState The new ConnectionClass.
         */
        fun onBandwidthStateChange(bandwidthState: ConnectionQuality)
    }

    /**
     * Method for adding new listeners to this class.
     *
     * @param listener [BandwidthStateListener] to add as a listener.
     */
    fun register(listener: BandwidthStateListener): ConnectionQuality {
        mListenerList.add(listener)
        return mCurrentBandwidthConnectionQuality.get()
    }

    /**
     * Method for removing listeners from this class.
     *
     * @param listener Reference to the [BandwidthStateListener] to be removed.
     */
    fun remove(listener: BandwidthStateListener) {
        mListenerList.remove(listener)
    }

    private fun notifyListeners() {
        mListenerList.forEach { it.onBandwidthStateChange(mCurrentBandwidthConnectionQuality.get()) }
    }

}