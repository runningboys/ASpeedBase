package com.common.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment

/**
 * 基类底部弹窗
 *
 * @author LiuFeng
 * @data 2021/10/15 15:47
 */
abstract class BaseBottomDialog : DialogFragment() {
    protected var mRootView: View? = null

    override fun onStart() {
        super.onStart()
        //得到dialog对应的window
        if (dialog != null && dialog!!.window != null) {
            val dialog = dialog
            val window = dialog!!.window
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) //去掉标题
            val params = window!!.attributes
            params.dimAmount = 0f
            params.gravity = Gravity.BOTTOM //修改gravity
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景透明
            window.attributes = params
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mRootView == null) {
            setContentView(inflater, container)
            initView()
            initListener()
        }
        return mRootView
    }

    protected open fun setContentView(inflater: LayoutInflater, container: ViewGroup?) {
        mRootView = inflater.inflate(layoutId, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mRootView != null && mRootView!!.parent != null) {
            (mRootView!!.parent as ViewGroup).removeView(mRootView)
        }
    }

    protected abstract val layoutId: Int

    protected abstract fun initView()

    protected abstract fun initListener()

    protected abstract fun initData()
}