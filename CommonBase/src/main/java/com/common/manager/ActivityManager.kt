package com.common.manager

import android.app.Activity
import android.app.ActivityManager.RunningTaskInfo
import android.content.Context
import android.os.Process
import java.util.Arrays
import java.util.Stack

/**
 * Activity管理器
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
class ActivityManager private constructor() {
    private var mActivityStack = Stack<Activity>()

    companion object {
        /**
         * 单一实例
         */
        @JvmStatic
        val instance = ActivityManager()
    }


    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity) {
        mActivityStack.add(activity)
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity? {
        return mActivityStack.lastElement()
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        val activity = mActivityStack.lastElement()
        this.finishActivity(activity)
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity) {
        if (mActivityStack.contains(activity)) {
            mActivityStack.remove(activity)
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
    }

    /**
     * 获取指定类名的Activity
     */
    fun findActivity(vararg activityClass: Class<out Activity>): Activity? {
        if (!mActivityStack.isEmpty()) {
            val list = activityClass.toSet()
            for (activity in mActivityStack) {
                if (list.contains(activity.javaClass)) {
                    return activity
                }
            }
        }
        return null
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(vararg activityClass: Class<out Activity>) {
        if (!mActivityStack.isEmpty()) {
            val list = activityClass.toSet()
            val iterator = mActivityStack.iterator()
            while (iterator.hasNext()) {
                val activity = iterator.next()
                if (list.contains(activity.javaClass)) {
                    iterator.remove()
                    activity.finish()
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        if (mActivityStack.size > 0) {
            for (activity in mActivityStack) {
                activity.finish()
            }
            mActivityStack.clear()
        }
    }

    /**
     * 退出应用程序
     */
    fun AppExit() {
        finishAllActivity()
        Process.killProcess(Process.myPid())
        System.exit(0)
    }

    /**
     * 是否退出应用程序
     *
     * @return
     */
    val isAppExit: Boolean
        get() = mActivityStack.isEmpty()

    /**
     * 判断某个activity是否在栈顶
     *
     * @param context
     * @param activityClass 某个activity
     *
     * @return
     */
    fun isTopActivity(context: Context, activityClass: Class<*>): Boolean {
        val activityName = activityClass.name
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        var runningTaskInfoList: List<RunningTaskInfo>? = null
        if (am != null) {
            runningTaskInfoList = am.getRunningTasks(1)
        }
        if (runningTaskInfoList != null && runningTaskInfoList.size > 0) {
            val cpn = runningTaskInfoList[0].topActivity
            return activityName == cpn!!.className
        }
        return false
    }

    val activitySizes: Int
        get() = mActivityStack.size
}