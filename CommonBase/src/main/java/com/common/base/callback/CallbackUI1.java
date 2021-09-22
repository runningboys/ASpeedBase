package com.common.base.callback;


import com.common.utils.UIHandler;

import androidx.annotation.NonNull;

/**
 * 1个参数，参数接口回调到UI线程
 *
 * @author LiuFeng
 * @data 2020/7/16 11:05
 */
public class CallbackUI1<T> implements CommonCallback1<T> {
    CommonCallback1<T> callback;

    public CallbackUI1(@NonNull CommonCallback1<T> callback) {
        this.callback = callback;
    }

    @Override
    public void onSuccess(T result) {
        if (callback != null) {
            UIHandler.run(() -> callback.onSuccess(result));
        }
    }

    @Override
    public void onError(int code, String desc) {
        if (callback != null) {
            UIHandler.run(() -> callback.onError(code, desc));
        }
    }
}
