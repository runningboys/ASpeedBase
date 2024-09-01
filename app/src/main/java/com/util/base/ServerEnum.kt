package com.util.base

import android.text.TextUtils

/**
 * 服务器环境配置枚举
 *
 * @author LiuFeng
 * @data 2022/5/12 19:34
 */
enum class ServerEnum {
    /**
     * 正式版
     */
    Release,

    /**
     * 测试版
     */
    Beta,

    /**
     * 开发版
     */
    Develop;

    companion object {
        fun parse(value: String): ServerEnum {
            for (type in values()) {
                if (TextUtils.equals(type.name, value)) {
                    return type
                }
            }
            return Release
        }
    }
}