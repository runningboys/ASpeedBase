package com.data.network.manager

import androidx.lifecycle.Observer
import com.common.base.ability.IBaseView
import com.common.data.NetResult
import com.common.data.NetStatus

/**
 * 网络请求的UI处理观察者
 *
 * @author LiuFeng
 * @data 2020/4/28 17:55
 */
abstract class UIObserver<T>(private val view: IBaseView?) : Observer<NetResult<T>> {
    override fun onChanged(tNetResult: NetResult<T>) {
        when (tNetResult.status) {
            NetStatus.Loading -> showLoading()
            NetStatus.Success -> {
                hideLoading()
                onSucceed(tNetResult.data)
            }

            NetStatus.Failed -> {
                hideLoading()
                onFailed(tNetResult.code, tNetResult.desc)
            }

            NetStatus.Complete -> hideLoading()
        }
    }

    protected fun showLoading() {
        view?.showLoading()
    }

    protected fun hideLoading() {
        view?.hideLoading()
    }

    protected abstract fun onSucceed(result: T)
    protected fun onFailed(code: Int, desc: String?) {
        view?.onError(code, desc)
    }
}