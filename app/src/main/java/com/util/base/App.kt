package com.util.base

import com.common.base.BaseApp
import com.data.database.DBHelper
import com.data.sp.AppSp


class App : BaseApp() {

    override fun onCreate() {
        super.onCreate()

        // 初始化应用管理器
        AppManager.init(this)

        // 用户已登录
        val userId = AppSp.getLoginUserId()
        if (userId.isNotBlank()) {
            AppSp.login(userId)

            // 初始DB的用户
            DBHelper.init(userId)
        }
    }
}