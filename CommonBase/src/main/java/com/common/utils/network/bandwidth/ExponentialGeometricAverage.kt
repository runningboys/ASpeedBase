package com.common.utils.network.bandwidth

import kotlin.math.ceil

/**
 * 指数几何平均值
 */
internal class ExponentialGeometricAverage(
        // 衰变常量
        private val mDecayConstant: Double
) {
    // Math.ceil向正无穷进一法取整
    private val mCutover = if (mDecayConstant == 0.0) Int.MAX_VALUE else ceil(1 / mDecayConstant).toInt()

    // 以bits/ms为单位的带宽测量值
    var average = -1.0
    private var mCount = 0


    /**
     * 为移动平均线添加一个新的度量。
     *
     * @param measurement 以bits/ms为单位的带宽测量，以增加移动平均。
     */
    fun addMeasurement(measurement: Double) {
        // 保持常量
        val keepConstant = 1 - mDecayConstant
        average = if (mCount > mCutover) {
            Math.exp(keepConstant * Math.log(average) + mDecayConstant * Math.log(measurement))
        } else if (mCount > 0) {
            // 保持变量（随着count变大逐渐逼近保持常量）
            val retained = keepConstant * mCount / (mCount + 1.0)
            // 衰变变量
            val newcomer = 1.0 - retained
            Math.exp(retained * Math.log(average) + newcomer * Math.log(measurement))
        } else {
            measurement
        }
        mCount++
    }


    /**
     * Reset the moving average.
     */
    fun reset() {
        average = -1.0
        mCount = 0
    }
}