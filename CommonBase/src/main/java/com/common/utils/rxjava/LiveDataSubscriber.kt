package com.common.utils.rxjava

import androidx.lifecycle.MutableLiveData
import com.common.data.NetResult
import rx.Observer

/**
 * 处理LiveData的订阅者
 *
 * @author LiuFeng
 * @data 2021/3/12 18:46
 */
class LiveDataSubscriber<T>(liveData: MutableLiveData<NetResult<T?>>) : Observer<T> {
    private val liveData: MutableLiveData<NetResult<T?>>

    init {
        this.liveData = liveData
    }

    override fun onNext(t: T) {
        liveData.value = NetResult.success(t)
    }

    override fun onError(e: Throwable) {
        val message = e.message
        liveData.value = NetResult.failed(0, message)
    }

    override fun onCompleted() {
        liveData.value = NetResult.complete<T>()
    }
}