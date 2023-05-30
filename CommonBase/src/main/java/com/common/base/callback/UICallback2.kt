package com.common.base.callback

import com.common.utils.UIHandler

/**
 * 2个参数，UI线程回调接口
 *
 * @author LiuFeng
 * @data 2020/7/16 11:05
 */
abstract class UICallback2<T1, T2> : CommonCallback2<T1, T2> {
    override fun onSuccess(result1: T1, result2: T2) {
        UIHandler.run { onSucceed(result1, result2) }
    }

    override fun onError(code: Int, desc: String?) {
        UIHandler.run { onFailed(code, desc) }
    }

    /**
     * UI线程回调成功
     *
     * @param result1
     * @param result2
     */
    abstract fun onSucceed(result1: T1, result2: T2)

    /**
     * UI线程回调失败
     *
     * @param code
     * @param desc
     */
    abstract fun onFailed(code: Int, desc: String?)
}