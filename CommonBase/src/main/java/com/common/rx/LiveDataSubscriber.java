package com.common.rx;

import com.common.api.NetResult;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import rx.Observer;

/**
 * 处理LiveData的订阅者
 *
 * @author LiuFeng
 * @data 2021/3/12 18:46
 */
public class LiveDataSubscriber<T> implements Observer<T> {
    private final MutableLiveData<NetResult<T>> liveData;


    public LiveDataSubscriber(@NonNull MutableLiveData<NetResult<T>> liveData) {
        this.liveData = liveData;
    }

    @Override
    public void onNext(T t) {
        liveData.setValue(NetResult.success(t));
    }

    @Override
    public void onError(Throwable e) {
        String message = e.getMessage();
        liveData.setValue(NetResult.failed(0, message));
    }

    @Override
    public void onCompleted() {
        liveData.setValue(NetResult.complete());
    }
}
