package com.common.base.callback

import com.common.utils.thread.UIHandler

/**
 * 1个参数，参数接口回调到UI线程
 *
 * @author LiuFeng
 * @data 2020/7/16 11:05
 */
class CallbackUI1<T>(val callback: CommonCallback1<T>) : CommonCallback1<T> {

    override fun onSuccess(result: T) {
        UIHandler.run { callback.onSuccess(result) }
    }

    override fun onError(code: Int, desc: String?) {
        UIHandler.run { callback.onError(code, desc) }
    }
}