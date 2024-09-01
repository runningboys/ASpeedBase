package com.util.base.ui.main

import android.content.Context
import com.common.base.ability.IBaseView
import com.common.base.mvp.BasePresenter

interface MainContract {

    interface View : IBaseView {
        fun onContacts()
    }

    abstract class Presenter(context: Context, view: View) : BasePresenter<View>(context, view) {
        /**
         * 查询联系人
         */
        abstract fun queryContacts()
    }
}