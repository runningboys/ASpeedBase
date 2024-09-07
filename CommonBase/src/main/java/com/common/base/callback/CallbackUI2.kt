package com.common.base.callback

import com.common.utils.thread.UIHandler

/**
 * 2个参数，参数接口回调到UI线程
 *
 * @author LiuFeng
 * @data 2020/7/16 11:05
 */
class CallbackUI2<T1, T2>(val callback: CommonCallback2<T1, T2>) : CommonCallback2<T1, T2> {

    override fun onSuccess(result1: T1, result2: T2) {
        UIHandler.run { callback.onSuccess(result1, result2) }
    }

    override fun onError(code: Int, desc: String?) {
        UIHandler.run { callback.onError(code, desc) }
    }
}