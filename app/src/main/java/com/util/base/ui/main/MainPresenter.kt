package com.util.base.ui.main

import android.content.Context
import com.util.base.ui.main.MainContract.Presenter

class MainPresenter(context: Context, view: MainContract.View) : Presenter(context, view) {
    override fun refreshToken() {}
}