package com.common.base.callback;

/**
 * 1个参数回调接口
 *
 * @author LiuFeng
 * @data 2020/7/16 11:08
 */
public interface CommonCallback1<T> extends Callback {

    /**
     * 回调成功
     *
     * @param result
     */
    void onSuccess(T result);
}
