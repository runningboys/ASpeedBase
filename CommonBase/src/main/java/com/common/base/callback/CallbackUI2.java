package com.common.base.callback;


import com.common.utils.UIHandler;

import androidx.annotation.NonNull;

/**
 * 2个参数，参数接口回调到UI线程
 *
 * @author LiuFeng
 * @data 2020/7/16 11:05
 */
public class CallbackUI2<T1, T2> implements CommonCallback2<T1, T2> {
    CommonCallback2<T1, T2> callback;

    public CallbackUI2(@NonNull CommonCallback2<T1, T2> callback) {
        this.callback = callback;
    }

    @Override
    public void onSuccess(T1 result1, T2 result2) {
        if (callback != null) {
            UIHandler.run(() -> callback.onSuccess(result1, result2));
        }
    }

    @Override
    public void onError(int code, String desc) {
        if (callback != null) {
            UIHandler.run(() -> callback.onError(code, desc));
        }
    }
}
