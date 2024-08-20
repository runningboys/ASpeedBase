package com.util.base

import com.common.base.BaseApplication
import com.data.database.DBHelper
import com.data.sp.AppSp


class App : BaseApplication() {

    override fun onCreate() {
        super.onCreate()

        val userId = AppSp.getLoginUserId()
        if (userId.isNotBlank()) {
            AppSp.login(userId)
            DBHelper.init(this, userId)
        }
    }
}