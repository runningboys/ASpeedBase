package com.util.base

import com.common.base.BaseApplication
import com.database.DBHelper
import com.util.base.data.AppSp


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