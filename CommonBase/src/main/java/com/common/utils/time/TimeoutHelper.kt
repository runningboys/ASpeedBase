package com.common.utils.time

import android.os.CountDownTimer
import com.common.utils.thread.UIHandler

/**
 * 超时辅助管理类
 *
 * @author LiuFeng
 * @data 2023/5/5 15:12
 */
object TimeoutHelper {
    private val timerMap = mutableMapOf<String, CountDownTimer>()


    /**
     * 启动计时器
     */
    fun startTimer(label: String, millis: Long, interval: Long = 1000, callback: () -> Unit) = UIHandler.run {
        var timer = timerMap[label]
        if (timer == null) {
            timer = object : CountDownTimer(millis, interval) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    stopTimer(label)
                    UIHandler.run { callback() }
                }
            }

            timerMap[label] = timer
            timer.start()
        }
    }


    /**
     * 停止计时器
     */
    fun stopTimer(label: String) = UIHandler.run {
        val timer = timerMap[label]
        timer?.run {
            cancel()
            timerMap.remove(label)
        }
    }


    /**
     * 是否有对应计时器
     */
    fun contains(label: String) : Boolean {
        return timerMap.contains(label)
    }


    /**
     * 清理全部计时器
     */
    fun clearAll() = UIHandler.run {
        for (timer in timerMap.values) {
            timer.cancel()
        }
        timerMap.clear()
    }
}