package com.common;

import android.app.Application;
import android.content.Context;

import com.common.receiver.NetworkStateReceiver;
import com.common.router.RouterUtil;

/**
 * Util初始化管理类
 *
 * @author liufeng
 * @date 2017-11-13
 */
public class CommonUtil {

    private static Context mContext;

    /**
     * 传入上下文，初始化数据
     *
     * @param application
     */
    public static void init(Application application) {
        mContext = application.getApplicationContext();

        // 初始化ARouter
        RouterUtil.init(application);

        // 注册网络状态变化广播接收器
        NetworkStateReceiver.getInstance().register(mContext);
    }

    /**
     * 获取上下文
     *
     * @return
     */
    public static Context getContext() {
        return mContext;
    }
}
