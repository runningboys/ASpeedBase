package com.common.base.callback;


import com.common.utils.UIHandler;

import androidx.annotation.NonNull;

/**
 * 3个参数，参数接口回调到UI线程
 *
 * @author LiuFeng
 * @data 2020/7/16 11:05
 */
public class CallbackUI3<T1, T2, T3> implements CommonCallback3<T1, T2, T3> {
    CommonCallback3<T1, T2, T3> callback;

    public CallbackUI3(@NonNull CommonCallback3<T1, T2, T3> callback) {
        this.callback = callback;
    }

    @Override
    public void onSuccess(T1 result1, T2 result2, T3 result3) {
        if (callback != null) {
            UIHandler.run(() -> callback.onSuccess(result1, result2, result3));
        }
    }

    @Override
    public void onError(int code, String desc) {
        if (callback != null) {
            UIHandler.run(() -> callback.onError(code, desc));
        }
    }
}
