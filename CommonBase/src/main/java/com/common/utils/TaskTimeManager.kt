package com.common.utils

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.DelayQueue
import java.util.concurrent.Delayed
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * 定时任务管理器
 *
 * @author LiuFeng
 * @data 2022/5/7 13:30
 */
object TaskTimeManager {
    // 任务是否在执行
    @Volatile
    private var isRunning = false

    // 执行任务采用单线程池
    private val executor: Executor = Executors.newSingleThreadExecutor()

    // 执行倒计时任务的延迟队列
    private val delayQueue = DelayQueue<DelayWrapper>()

    // 存储任务id
    private val taskMap = ConcurrentHashMap<String, Task>()


    fun addTask(task: Task) {
        // 记录任务
        taskMap[task.id] = task

        // 放入延迟队列
        delayQueue.offer(DelayWrapper(task))

        // 执行队列任务
        executeTask()
    }


    fun removeTask(taskId: String) {
        taskMap.remove(taskId)
        delayQueue.removeIf { it.task.id == taskId }
    }


    fun getTask(taskId: String): Task? {
        return taskMap[taskId]
    }

    fun getAllTask(): List<Task> {
        return taskMap.values.toList()
    }


    fun contains(taskId: String): Boolean {
        return taskMap.containsKey(taskId)
    }


    fun clearAll() {
        taskMap.clear()
        delayQueue.clear()
    }


    /**
     * 执行倒计时任务，执行结束后自动退出
     */
    private fun executeTask() {
        if (isRunning) return

        isRunning = true
        executor.execute {
            while (!delayQueue.isEmpty()) {
                try {
                    // 获取阻塞，到时间再获取
                    val wrapper = delayQueue.take()
                    val task = wrapper.task
                    taskMap.remove(task.id)
                    UIHandler.run { task.runnable.run() }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    break
                }
            }
            isRunning = false
        }
    }

    /**
     * 对消息的延迟包装类
     */
    private class DelayWrapper(val task: Task) : Delayed {
        override fun getDelay(unit: TimeUnit): Long {
            return unit.convert(getRetainTime(), TimeUnit.MILLISECONDS)
        }

        /**
         * 当前任务剩余时间
         *
         * @return
         */
        private fun getRetainTime(): Long {
            val duration = task.duration
            val startTime = task.startTime
            val endTime = startTime + duration
            val currentTime = System.currentTimeMillis()
            return endTime - currentTime
        }

        override fun compareTo(other: Delayed): Int {
            // 按时间增序
            return getDelay(TimeUnit.MILLISECONDS).compareTo(other.getDelay(TimeUnit.MILLISECONDS))
        }
    }
}


data class Task(val id: String, val startTime: Long, val duration: Long, val runnable: Runnable)