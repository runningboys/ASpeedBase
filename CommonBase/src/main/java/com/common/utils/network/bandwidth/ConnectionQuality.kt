package com.common.utils.network.bandwidth

/**
 * 对不同网络连接质量的一般枚举
 */
enum class ConnectionQuality {
    /**
     * 带宽低于 150 kbps.
     */
    POOR,

    /**
     * 带宽在150到550 kbps之间
     */
    MODERATE,

    /**
     * 带宽在550到2000 kbps之间
     */
    GOOD,

    /**
     * 带宽超过 2000 kbps
     */
    EXCELLENT,

    /**
     * 未知带宽的占位符。这是初始值并保持在这个值上
     * 如果不能准确地找到带宽。
     */
    UNKNOWN
}