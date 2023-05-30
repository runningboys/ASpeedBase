package com.common.base.callback

/**
 * 2个参数回调接口
 *
 * @author LiuFeng
 * @data 2020/7/16 11:08
 */
interface CommonCallback2<T1, T2> : Callback {
    /**
     * 回调成功
     *
     * @param result1
     * @param result2
     */
    fun onSuccess(result1: T1, result2: T2)
}