package com.common.rx;

import com.common.utils.log.LogUtil;

import rx.Observable;
import rx.Subscriber;

/**
 * 数据库的订阅Action
 *
 * @author LiuFeng
 * @data 2020/2/8 11:36
 */
public abstract class OnSubscribe<T> implements Observable.OnSubscribe<T> {

    @Override
    public void call(Subscriber<? super T> subscriber) {
        try {
            T t = get();
            subscriber.onNext(t);
        } catch (Exception e) {
            subscriber.onError(e);
            LogUtil.e(e);
        } finally {
            subscriber.onCompleted();
        }
    }

    /**
     * 得到数据库返回值
     *
     * @param
     */
    protected abstract T get();
}
