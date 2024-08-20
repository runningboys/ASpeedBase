package com.common.utils

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.common.CommonUtil
import com.common.base.R

/**
 * 吐丝提示工具类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
object ToastUtil {
    // 吐司布局Padding
    private var left = 0
    private var top = 0
    private var right = 0
    private var bottom = 0

    // 类加载初始化
    init {
        left = DeviceUtil.dp2px(12f)
        top = DeviceUtil.dp2px(10f)
        right = DeviceUtil.dp2px(12f)
        bottom = DeviceUtil.dp2px(10f)
    }

    private fun getContext(): Context {
        return CommonUtil.getContext()
    }

    /**
     * 显示吐司
     *
     * @param message 消息
     */
    fun showToast(message: String) {
        makeText(0, message, 3000)
    }

    /**
     * 显示吐司
     *
     * @param messageResId 消息资源ID
     */
    fun showToast(messageResId: Int) {
        makeText(0, getContext().resources.getString(messageResId), 3000)
    }

    /**
     * 显示吐司
     *
     * @param message  消息
     * @param duration 间隔时间 单位：ms
     */
    fun showToast(message: String, duration: Int) {
        makeText(0, message, duration)
    }

    /**
     * 显示吐司
     *
     * @param imageResId 图片资源ID
     * @param message    消息
     */
    fun showToast(imageResId: Int, message: String) {
        makeText(imageResId, message, 3000)
    }

    /**
     * 显示吐司
     *
     * @param imageResId
     * @param messageResId
     */
    fun showToast(imageResId: Int, messageResId: Int) {
        makeText(imageResId, getContext().resources.getString(messageResId), 3000)
    }

    /**
     * 显示吐司
     *
     * @param messageResId
     * @param duration
     */
    fun showToastTime(messageResId: Int, duration: Int) {
        makeText(0, getContext().resources.getString(messageResId), duration)
    }

    /**
     * 自定义Toast样式
     *
     * @param imageResId 图片Id
     * @param text       消息文本
     * @param duration   间隔时间 单位：ms
     */
    private fun makeText(imageResId: Int, text: CharSequence, duration: Int) {
        UIHandler.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //Android 11以上不允许后台弹出自定义Toast
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show()
                return@run
            }

            // 由layout文件创建一个View对象
            val layout = LinearLayout(getContext())
            layout.setBackgroundResource(R.drawable.toast_default_bg)
            layout.alpha = 0.8f
            layout.gravity = Gravity.CENTER
            layout.setPadding(left, top, right, bottom)

            // 可显示带图的toast
            if (imageResId != 0) {
                // 实例化ImageView对象
                val imageView = ImageView(getContext())
                imageView.setImageResource(imageResId)
                layout.addView(imageView)
            }

            // 实例化TextView对象
            val textView = TextView(getContext())
            textView.text = text
            textView.textSize = 14f
            textView.setTextColor(Color.WHITE)
            textView.gravity = Gravity.CENTER
            layout.addView(textView)

            val result = Toast(getContext())
            result.view = layout
            result.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            result.duration = duration
            result.show()
        }
    }
}