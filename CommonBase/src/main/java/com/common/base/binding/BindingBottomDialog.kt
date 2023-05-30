package com.common.base.binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.common.base.BaseBottomDialog
import com.common.base.ability.IBinding

/**
 * 带Binding的基类底部弹窗
 *
 * @author LiuFeng
 * @data 2021/9/22 16:20
 */
abstract class BindingBottomDialog<B : ViewBinding> : BaseBottomDialog(), IBinding<B> {
    protected var binding: B? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = getViewBinding()
        super.onCreate(savedInstanceState)
    }

    /**
     * 当binding非空时，用binding的rootView来设置；
     * 当binding为空时，则用布局id设置；
     *
     * @param inflater
     * @param container
     */
    override fun setContentView(inflater: LayoutInflater, container: ViewGroup?) {
        if (binding != null) {
            mRootView = binding!!.root
        } else {
            super.setContentView(inflater, container)
        }
    }
}