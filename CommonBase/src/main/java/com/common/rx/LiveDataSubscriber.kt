package com.common.rx

import androidx.lifecycle.MutableLiveData
import com.common.api.NetResult
import com.common.api.NetResult.Companion.complete
import com.common.api.NetResult.Companion.failed
import com.common.api.NetResult.Companion.success
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
        liveData.value = success(t)
    }

    override fun onError(e: Throwable) {
        val message = e.message
        liveData.value = failed<T>(0, message)
    }

    override fun onCompleted() {
        liveData.value = complete<T>()
    }
}