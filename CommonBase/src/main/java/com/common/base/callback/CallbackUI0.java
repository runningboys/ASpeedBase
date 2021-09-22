package com.common.base.callback;


import com.common.utils.UIHandler;

import androidx.annotation.NonNull;

/**
 * 0个参数，参数接口回调到UI线程
 *
 * @author LiuFeng
 * @data 2020/7/16 11:05
 */
public class CallbackUI0 implements CommonCallback0 {
    CommonCallback0 callback;

    public CallbackUI0(@NonNull CommonCallback0 callback) {
        this.callback = callback;
    }

    @Override
    public void onSuccess() {
        if (callback != null) {
            UIHandler.run(() -> callback.onSuccess());
        }
    }

    @Override
    public void onError(int code, String desc) {
        if (callback != null) {
            UIHandler.run(() -> callback.onError(code, desc));
        }
    }
}
