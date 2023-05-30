package com.common.router

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.common.base.BuildConfig

/**
 * 路由工具类
 *
 * @author LiuFeng
 * @date 2018-8-15
 */
object RouterUtil {
    /**
     * 初始化Router
     *
     * @param application
     */
    fun init(application: Application) {
        if (BuildConfig.DEBUG) {
            // 打印日志
            ARouter.openLog()
            // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug()
        }
        ARouter.init(application)
    }

    /**
     * 使用 [ARouter] 根据 `path` 跳转到对应的页面, 这个方法因为没有使用 [Activity]跳转
     * 所以 [ARouter] 会自动给 [android.content.Intent] 加上 Intent.FLAG_ACTIVITY_NEW_TASK
     * 如果不想自动加上这个 Flag 请使用 [ARouter.getInstance] 并传入 [Activity]
     *
     * @param path `path`
     */
    fun navigation(path: String) {
        ARouter.getInstance().build(path).navigation()
    }

    /**
     * 使用 [ARouter] 根据 `path` 跳转到对应的页面, 如果参数 `context` 传入的不是 [Activity]
     * [ARouter] 就会自动给 [android.content.Intent] 加上 Intent.FLAG_ACTIVITY_NEW_TASK
     * 如果不想自动加上这个 Flag 请使用 [Activity] 作为 `context` 传入
     *
     * @param context
     * @param path
     */
    fun navigation(context: Context, path: String) {
        ARouter.getInstance().build(path).navigation(context)
    }

    /**
     * 使用 [ARouter] 根据 `path` 跳转到对应的页面, 这个方法因为没有使用 [Activity]跳转
     * 所以 [ARouter] 会自动给 [android.content.Intent] 加上 Intent.FLAG_ACTIVITY_NEW_TASK
     * 如果不想自动加上这个 Flag 请使用 [ARouter.getInstance] 并传入 [Activity]
     *
     * @param path `path`
     */
    fun navigation(path: String, bundle: Bundle) {
        ARouter.getInstance().build(path).withBundle("bundle", bundle).navigation()
    }

    /**
     * 使用 [ARouter] 根据 `path` 跳转到对应的页面, 如果参数 `context` 传入的不是 [Activity]
     * [ARouter] 就会自动给 [android.content.Intent] 加上 Intent.FLAG_ACTIVITY_NEW_TASK
     * 如果不想自动加上这个 Flag 请使用 [Activity] 作为 `context` 传入
     *
     * @param bundle  传值的bundle
     * @param context
     * @param path
     */
    fun navigation(context: Context, bundle: Bundle, path: String) {
        ARouter.getInstance().build(path).withBundle("bundle", bundle).navigation(context)
    }

    fun navigation(context: Activity, path: String, requestCode: Int) {
        ARouter.getInstance().build(path).navigation(context, requestCode)
    }
}