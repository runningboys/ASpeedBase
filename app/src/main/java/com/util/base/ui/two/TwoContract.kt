package com.util.base.ui.two

import android.content.Context
import com.common.base.ability.IBaseView
import com.common.base.mvp.BasePresenter

/**
 * 契约接口，用于分离View和Presenter
 */
interface TwoContract {

    /**
     * View层接口
     */
    interface View : IBaseView {
        fun onLogin()
    }


    /**
     * Presenter层接口
     */
    abstract class Presenter(context: Context, view: View) : BasePresenter<View>(context, view) {
        abstract fun login(phone: String, password: String)
    }
}