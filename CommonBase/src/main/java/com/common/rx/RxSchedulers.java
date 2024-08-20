package com.common.rx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import rx.Observable;

/**
 * rx线程调度工具类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public class RxSchedulers {

    private static volatile Scheduler ioScheduler;

    static {
        int maximumThreadSize = 2 * (Runtime.getRuntime().availableProcessors() > 0 ? Runtime.getRuntime().availableProcessors() : 2) + 1;
        ExecutorService executor = new ThreadPoolExecutor(2, maximumThreadSize, 20L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(64), new ThreadPoolExecutor.DiscardOldestPolicy());
        ioScheduler = Schedulers.from(executor);
    }

    /**
     * 有边界的io线程池Scheduler
     * <p>
     * 备注：rx自带的Schedules.io是无边界线程池，
     * 易导致栈内存泄露，所有这里条提供一个有边界线程池
     *
     * @return
     */
    public static Scheduler io() {
        return ioScheduler;
    }

    /**
     * 主线程
     *
     * @return
     */
    public static Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

    /**
     * 从io线程切换到android主线程
     *
     * @param <T>
     * @return
     */
    /*public static <T> Observable.Transformer<T, T> io_main() {
        return tObservable -> tObservable.subscribeOn(io()).unsubscribeOn(io()).observeOn(mainThread());
    }*/
}
