package com.common.base.callback

import com.common.utils.UIHandler

/**
 * 0个参数，UI线程回调接口
 *
 * @author LiuFeng
 * @data 2020/7/16 11:05
 */
abstract class UICallback0 : CommonCallback0 {
    override fun onSuccess() {
        UIHandler.run { onSucceed() }
    }

    override fun onError(code: Int, desc: String?) {
        UIHandler.run { onFailed(code, desc) }
    }

    /**
     * UI线程回调成功
     */
    abstract fun onSucceed()

    /**
     * UI线程回调失败
     *
     * @param code
     * @param desc
     */
    abstract fun onFailed(code: Int, desc: String?)
}