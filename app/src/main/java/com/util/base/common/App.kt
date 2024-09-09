package com.util.base.common

import com.common.base.BaseApp
import com.util.base.AppManager


class App : BaseApp() {

    override fun onCreate() {
        super.onCreate()

        // 初始化应用管理器
        AppManager.init(this)
    }
}