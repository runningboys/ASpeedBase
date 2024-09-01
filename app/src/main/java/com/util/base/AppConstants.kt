package com.util.base

/**
 * 常量信息
 *
 * @author LiuFeng
 * @data 2022/5/23 16:17
 */
interface AppConstants {
    /**
     * 正式服环境
     */
    object Release {
        const val BASE_URL = "wss://test.devplay.cc/ws?"
        const val MEETING_URL = "wss://workers.devplay.cc/websocket"
    }

    /**
     * 测试服环境
     */
    object Beta {
        const val BASE_URL = "wss://test.devplay.cc/ws?"
        const val MEETING_URL = "wss://workers.devplay.cc/websocket"
    }

    /**
     * 开发服环境
     */
    object Develop {
        const val BASE_URL = "wss://data.devplay.cc/ws?"
        const val MEETING_URL = "wss://workers.devplay.cc/websocket"
    }
}