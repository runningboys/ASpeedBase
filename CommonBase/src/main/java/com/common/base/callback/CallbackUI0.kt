package com.common.base.callback

import com.common.utils.thread.UIHandler

/**
 * 0个参数，参数接口回调到UI线程
 *
 * @author LiuFeng
 * @data 2020/7/16 11:05
 */
class CallbackUI0(val callback: CommonCallback0) : CommonCallback0 {

    override fun onSuccess() {
        UIHandler.run { callback.onSuccess() }
    }

    override fun onError(code: Int, desc: String?) {
        UIHandler.run { callback.onError(code, desc) }
    }
}