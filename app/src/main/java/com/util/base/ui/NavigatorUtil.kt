package com.util.base.ui

import android.content.Context
import android.content.Intent
import com.util.base.ui.main.MainActivity
import com.util.base.ui.one.OneActivity
import com.util.base.ui.three.ThreeActivity
import com.util.base.ui.two.TwoActivity

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
     * 跳转OneActivity
     */
    fun toOne(context: Context) {
        val intent = Intent(context, OneActivity::class.java)
        context.startActivity(intent)
    }

    /**
     * 跳转TwoActivity
     */
    fun toTwo(context: Context) {
        val intent = Intent(context, TwoActivity::class.java)
        context.startActivity(intent)
    }

    /**
     * 跳转ThreeActivity
     */
    fun toThree(context: Context) {
        val intent = Intent(context, ThreeActivity::class.java)
        context.startActivity(intent)
    }
}