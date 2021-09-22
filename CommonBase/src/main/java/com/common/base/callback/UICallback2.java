package com.common.base.callback;


import com.common.utils.UIHandler;

/**
 * 2个参数，UI线程回调接口
 *
 * @author LiuFeng
 * @data 2020/7/16 11:05
 */
public abstract class UICallback2<T1, T2> implements CommonCallback2<T1, T2> {

    @Override
    public void onSuccess(T1 result1, T2 result2) {
        UIHandler.run(() -> onSucceed(result1, result2));
    }

    @Override
    public void onError(int code, String desc) {
        UIHandler.run(() -> onFailed(code, desc));
    }

    /**
     * UI线程回调成功
     *
     * @param result1
     * @param result2
     */
    public abstract void onSucceed(T1 result1, T2 result2);

    /**
     * UI线程回调失败
     *
     * @param code
     * @param desc
     */
    public abstract void onFailed(int code, String desc);
}
