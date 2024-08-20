package com.common.rx;

import com.common.utils.log.LogUtil;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;


/**
 * 数据库的订阅Action
 *
 * @author LiuFeng
 * @data 2020/2/8 11:36
 */
public abstract class OnSubscribe<T> implements ObservableOnSubscribe<T> {

    @Override
    public void subscribe(@NonNull ObservableEmitter<T> emitter) throws Throwable {
        try {
            T t = get();
            emitter.onNext(t);
        } catch (Exception e) {
            emitter.onError(e);
            LogUtil.e(e);
        } finally {
            emitter.onComplete();
        }
    }

    /**
     * 得到数据库返回值
     *
     * @param
     */
    protected abstract T get();
}
