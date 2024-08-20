package com.common.data

/**
 * 网络请求状态
 *
 * @author LiuFeng
 * @data 2020/4/28 17:09
 */
enum class NetStatus {
    /**
     * 加载中
     */
    Loading,

    /**
     * 加载完成
     */
    Complete,

    /**
     * 成功
     */
    Success,

    /**
     * 失败
     */
    Failed
}