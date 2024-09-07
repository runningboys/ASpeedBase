package com.common.base.callback

import com.common.utils.thread.UIHandler

/**
 * 3个参数，参数接口回调到UI线程
 *
 * @author LiuFeng
 * @data 2020/7/16 11:05
 */
class CallbackUI3<T1, T2, T3>(val callback: CommonCallback3<T1, T2, T3>) : CommonCallback3<T1, T2, T3> {

    override fun onSuccess(result1: T1, result2: T2, result3: T3) {
        UIHandler.run { callback.onSuccess(result1, result2, result3) }
    }

    override fun onError(code: Int, desc: String?) {
        UIHandler.run { callback.onError(code, desc) }
    }
}