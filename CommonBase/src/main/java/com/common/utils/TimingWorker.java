package com.common.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 时间控制任务工具类
 *
 * @author LiuFeng
 * @data 2021/2/26 17:49
 */
public class TimingWorker {
    // 时间容器
    private static final Map<String, Long> timeMap = new ConcurrentHashMap<>();

    /**
     * 添加最小时间的一个任务
     *
     * @param label
     * @param minTime
     */
    public static void addMinTimeTask(String label, long minTime) {
        long endTaskTime = System.currentTimeMillis() + minTime;
        timeMap.put(label, endTaskTime);
    }

    /**
     * 执行最小时间的任务
     *
     * @param label
     * @param runnable
     */
    public static void executeMinTimeTask(String label, Runnable runnable) {
        long currentTime = System.currentTimeMillis();
        long endTaskTime = timeMap.remove(label);

        // 计算到最小结束任务时间的差值
        long diff = currentTime - endTaskTime;
        if (diff < 0) {
            UIHandler.run(runnable, -diff);
            return;
        }

        UIHandler.run(runnable);
    }
}
