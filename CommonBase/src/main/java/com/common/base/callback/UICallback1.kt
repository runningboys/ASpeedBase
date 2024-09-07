package com.common.base.callback

import com.common.utils.thread.UIHandler

/**
 * 1个参数，UI线程回调接口
 *
 * @author LiuFeng
 * @data 2020/7/16 11:05
 */
abstract class UICallback1<T> : CommonCallback1<T> {
    override fun onSuccess(result: T) {
        UIHandler.run { onSucceed(result) }
    }

    override fun onError(code: Int, desc: String?) {
        UIHandler.run { onFailed(code, desc) }
    }

    /**
     * UI线程回调成功
     *
     * @param result
     */
    abstract fun onSucceed(result: T)

    /**
     * UI线程回调失败
     *
     * @param code
     * @param desc
     */
    abstract fun onFailed(code: Int, desc: String?)
}