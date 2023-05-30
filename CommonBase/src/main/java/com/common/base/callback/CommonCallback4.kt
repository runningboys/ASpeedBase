package com.common.base.callback

/**
 * 4个参数回调接口
 *
 * @author LiuFeng
 * @data 2020/7/16 11:08
 */
interface CommonCallback4<T1, T2, T3, T4> : Callback {
    /**
     * 回调成功
     *
     * @param result1
     * @param result2
     * @param result3
     * @param result4
     */
    fun onSuccess(result1: T1, result2: T2, result3: T3, result4: T4)
}