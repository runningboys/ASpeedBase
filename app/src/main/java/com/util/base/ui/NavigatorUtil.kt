package com.util.base.ui

import android.content.Context
import android.content.Intent
import com.util.base.ui.main.MainActivity
import com.util.base.ui.other.OtherActivity

/**
 * 页面导航工具
 */
object NavigatorUtil {

    /**
     * 跳转首页
     */
    fun toMain(context: Context) {
        context.startActivity(Intent(context, MainActivity::class.java))
    }

    /**
     * 打开其他
     */
    fun toOther(context: Context) {
        val intent = Intent(context, OtherActivity::class.java)
        context.startActivity(intent)
    }
}