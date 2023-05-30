package com.common.base.callback

/**
 * 1个参数回调接口
 *
 * @author LiuFeng
 * @data 2020/7/16 11:08
 */
interface CommonCallback1<T> : Callback {
    /**
     * 回调成功
     *
     * @param result
     */
    fun onSuccess(result: T)
}