package com.common.utils.thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UI数据刷新操作
 *
 * @author LiuFeng
 * @data 2022/4/29 10:55
 */
public class UIHandler extends Handler {
    private static final Map<Integer, Runnable> taskMap = new ConcurrentHashMap<>();

    private UIHandler() {
        super(Looper.getMainLooper());
    }

    /**
     * Handler单实例
     *
     * @return
     */
    public static UIHandler getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final UIHandler INSTANCE = new UIHandler();
    }

    /**
     * 运行在主线程
     *
     * @param runnable
     */
    public static void run(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            getInstance().post(runnable);
        }
    }

    /**
     * 运行在主线程(延迟执行)
     *
     * @param runnable
     * @param delayMillis
     */
    public static void run(Runnable runnable, long delayMillis) {
        getInstance().postDelayed(runnable, delayMillis);
    }

    /**
     * 运行在主线程(延迟执行)
     *
     * @param tag 任务的唯一标记，实现新的任务覆盖旧的未执行任务
     * @param runnable
     * @param delayMillis
     */
    public static void run(String tag, Runnable runnable, long delayMillis) {
        int what = tag.hashCode();
        if (taskMap.containsKey(what)) {
            getInstance().removeMessages(what);
        }
        taskMap.put(what, runnable);
        getInstance().sendEmptyMessageDelayed(what, delayMillis);
    }

    /**
     * 移除任务
     *
     * @param tag
     */
    public static void removeTask(String tag) {
        int what = tag.hashCode();
        if (taskMap.containsKey(what)) {
            taskMap.remove(what);
            getInstance().removeMessages(what);
        }
    }

    /**
     * 是否主线程
     *
     * @return
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 清空当前Handler队列所有消息
     */
    public static void dispose() {
        getInstance().removeCallbacksAndMessages(null);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if (taskMap.containsKey(msg.what)) {
            taskMap.remove(msg.what).run();
        }
    }
}
