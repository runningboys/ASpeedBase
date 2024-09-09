package com.util.base.common

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
        const val BASE_URL = "https://www.baidu.com"
        const val USER_AGREEMENT_URL = "https://www.baidu.com"
    }

    /**
     * 测试服环境
     */
    object Beta {
        const val BASE_URL = "https://www.baidu.com"
        const val USER_AGREEMENT_URL = "https://www.baidu.com"
    }

    /**
     * 开发服环境
     */
    object Develop {
        const val BASE_URL = "https://www.baidu.com"
        const val USER_AGREEMENT_URL = "https://www.baidu.com"
    }
}