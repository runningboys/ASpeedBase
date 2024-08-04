package com.util.base.ui.main

import android.content.Context
import com.common.base.ability.IBaseView
import com.common.base.BasePresenter

interface MainContract {

    interface View : IBaseView {
        fun showTips(message: String)
    }

    abstract class Presenter(context: Context, view: View) : BasePresenter<View>(context, view) {
        /**
         * 刷新token
         */
        abstract fun refreshToken()
    }
}