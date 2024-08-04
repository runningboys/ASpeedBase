package com.common.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import androidx.multidex.MultiDexApplication
import com.common.CommonUtil
import com.common.utils.log.LogUtil

/**
 * BaseApplication
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
open class BaseApplication : MultiDexApplication(), ActivityLifecycleCallbacks {
    // activity计数器
    private var mActivityCount = 0

    override fun onCreate() {
        super.onCreate()
        context = this
        // 注册App所有Activity的生命周期回调监听器
        registerActivityLifecycleCallbacks(this)
        CommonUtil.init(this)
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        mActivityCount++
        if (0 == mActivityCount - 1) {
            LogUtil.i("App 从后台回到前台了" + activity.localClassName)
        }
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {
        mActivityCount--
        if (0 == mActivityCount) {
            LogUtil.i("App 从前台切换到后台了" + activity.localClassName)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    companion object {
        /**
         * 获取上下文
         *
         * @return
         */
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
    }
}