package com.common.base.callback

/**
 * 5个参数回调接口
 *
 * @author LiuFeng
 * @data 2020/7/16 11:08
 */
interface CommonCallback5<T1, T2, T3, T4, T5> : Callback {
    /**
     * 回调成功
     *
     * @param result1
     * @param result2
     * @param result3
     * @param result4
     * @param result5
     */
    fun onSuccess(result1: T1, result2: T2, result3: T3, result4: T4, result5: T5)
}