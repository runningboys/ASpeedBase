package com.common.rx.subscriber;

import com.common.data.ApiException;
import com.common.data.NoNetworkException;
import com.common.utils.ToastUtil;
import com.common.utils.log.LogUtil;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * 基础通用订阅者
 *
 * @author LiuFeng
 * @data 2019/1/28 19:31
 */
public abstract class BaseSubscriber<T> extends Subscriber<T> {
    private static final String TAG = "BaseSubscriber";

    private StackTraceElement[] stackTrace;

    public BaseSubscriber() {
        this.stackTrace = new Exception().getStackTrace();
    }

    @Override
    public void onNext(T t) {
    }

    @Override
    public void onError(Throwable e) {
        LogUtil.e(TAG, e.getMessage(), stackTrace, 3, 2);

        int code;
        String desc;

        // api请求异常
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            code = apiException.getCode();
            desc = apiException.getDesc();
        }
        // 无网络异常
        else if (e instanceof NoNetworkException) {
            code = -1;
            desc = "无网络连接，请检查您的网络";
        }
        // http异常
        else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            code = httpException.code();
            desc = httpException.message();
        }
        // 其他异常
        else {
            code = -1;
            desc = e.getMessage();
        }

        onFailed(code, desc);
    }

    /**
     * 覆写此方法比onError更友好，直接获取错误描述
     *
     * @param code
     * @param desc
     */
    public void onFailed(int code, String desc) {
//        ToastUtil.showToast(desc);
    }

    @Override
    public void onCompleted() {
    }
}
