package com.common.base.binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.common.base.BaseFragment

/**
 * 带Binding的基类Fragment
 *
 * @author LiuFeng
 * @data 2021/9/22 16:19
 */
abstract class BindingFragment<VB : ViewBinding> : BaseFragment(), IBinding<VB> {
    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    override fun getLayoutId(): Int = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding  = getViewBinding(this, inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}