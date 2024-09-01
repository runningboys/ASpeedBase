package com.common.base.binding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.common.ext.inflateBindingWithGeneric

/**
 * ViewBinding集成接口
 *
 * @author LiuFeng
 * @data 2021/10/15 14:47
 */
interface IBinding<VB : ViewBinding> {

    fun getViewBinding(activity: AppCompatActivity): VB {
        return activity.inflateBindingWithGeneric(activity.layoutInflater)
    }

    fun getViewBinding(fragment: Fragment, inflater: LayoutInflater, container: ViewGroup?): VB {
        return fragment.inflateBindingWithGeneric(inflater, container, false)
    }
}