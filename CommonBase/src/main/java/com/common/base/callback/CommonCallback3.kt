package com.common.base.callback

/**
 * 3个参数回调接口
 *
 * @author LiuFeng
 * @data 2020/7/16 11:08
 */
interface CommonCallback3<T1, T2, T3> : Callback {
    /**
     * 回调成功
     *
     * @param result1
     * @param result2
     * @param result3
     */
    fun onSuccess(result1: T1, result2: T2, result3: T3)
}