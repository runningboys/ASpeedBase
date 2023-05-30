package com.common.base.callback

/**
 * 基类回调接口
 * 描述：所有CommonCallback接口都必须继承自它
 *
 * @author LiuFeng
 * @data 2020/7/16 11:03
 */
interface Callback {
    /**
     * 失败回调
     *
     * @param code
     * @param desc
     */
    fun onError(code: Int, desc: String?)
}