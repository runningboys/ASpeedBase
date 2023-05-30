package com.common.base.callback

/**
 * 简化回调接口
 *
 * @author LiuFeng
 * @data 2020/7/16 11:08
 */
class SimpleCallback2<T1, T2> : CommonCallback2<T1, T2> {
    override fun onSuccess(result1: T1, result2: T2) {}
    override fun onError(code: Int, desc: String?) {}
}