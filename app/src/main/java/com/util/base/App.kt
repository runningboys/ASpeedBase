package com.util.base

import com.common.base.BaseApp
import com.data.database.DBHelper
import com.data.sp.AppSp


class App : BaseApp() {

    override fun onCreate() {
        super.onCreate()

        val userId = AppSp.getLoginUserId()
        if (userId.isNotBlank()) {
            AppSp.login(userId)
            DBHelper.init(this, userId)
        }
    }
}