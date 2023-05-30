package com.common.base.callback

/**
 * 简化回调接口
 *
 * @author LiuFeng
 * @data 2020/7/16 11:05
 */
class SimpleCallback0 : CommonCallback0 {
    override fun onSuccess() {}
    override fun onError(code: Int, desc: String?) {}
}