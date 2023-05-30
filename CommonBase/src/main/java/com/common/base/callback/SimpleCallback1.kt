package com.common.base.callback

/**
 * 简化回调接口
 *
 * @author LiuFeng
 * @data 2020/7/16 11:08
 */
class SimpleCallback1<T> : CommonCallback1<T> {
    override fun onSuccess(result: T) {}
    override fun onError(code: Int, desc: String?) {}
}