package com.util.base.skin

import android.app.Activity
import android.content.Intent

/**
 * 皮肤测试
 *
 * @author LiuFeng
 * @data 2021/9/23 11:57
 */
object SkinTest {
    fun runTest(activity: Activity) {
        UIManager.instanceUI()
        val intent = Intent(activity, TestSkinActivity::class.java)
        activity.startActivity(intent)
    }
}