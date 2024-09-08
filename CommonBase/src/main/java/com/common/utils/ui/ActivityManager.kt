package com.common.utils.ui

import android.app.Activity
import android.os.Process
import com.blankj.utilcode.util.ActivityUtils

/**
 * Activity管理器
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
object ActivityManager {


    /**
     * 获取当前Activity
     */
    fun currentActivity(): Activity {
        return ActivityUtils.getTopActivity()
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity) {
        ActivityUtils.finishActivity(activity)
    }

    /**
     * 获取指定类名的Activity
     */
    fun getActivity(vararg activityClass: Class<out Activity>): Activity? {
        val list = activityClass.toSet()
        val activityList = ActivityUtils.getActivityList()
        for (activity in activityList) {
            if (list.contains(activity.javaClass)) {
                return activity
            }
        }
        return null
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(vararg activityClass: Class<out Activity>) {
        val list = activityClass.toSet()
        val activityList = ActivityUtils.getActivityList()
        for (activity in activityList) {
            if (list.contains(activity.javaClass)) {
                finishActivity(activity)
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivities() {
        ActivityUtils.finishAllActivities()
    }

    /**
     * 退出应用程序
     */
    fun exitApp() {
        finishAllActivities()
        Process.killProcess(Process.myPid())
        System.exit(0)
    }
}