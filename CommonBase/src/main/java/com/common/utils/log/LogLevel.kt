package com.common.utils.log

import android.util.Log

/**
 * 日志等级
 *
 * @author LiuFeng
 * @date 2018-9-01
 */
enum class LogLevel(val code: Int) {
    /**
     * VERBOSE 等级。
     */
    VERBOSE(Log.VERBOSE),

    /**
     * Debug 等级。
     */
    DEBUG(Log.DEBUG),

    /**
     * Info 等级。
     */
    INFO(Log.INFO),

    /**
     * Warn 等级。
     */
    WARN(Log.WARN),

    /**
     * Error 等级。
     */
    ERROR(Log.ERROR),

    /**
     * ASSERT 等级。
     */
    ASSERT(Log.ASSERT);

    companion object {
        /**
         * 解析相应等级
         *
         * @param code
         *
         * @return
         */
        fun parse(code: Int): LogLevel {
            for (level in values()) {
                if (level.code == code) {
                    return level
                }
            }
            throw IllegalArgumentException("LogLevel code is illegal.")
        }
    }
}