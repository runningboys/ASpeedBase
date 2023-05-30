package com.util.base

import android.content.Context
import com.util.base.MainContract.Presenter

class MainPresenter(context: Context, view: MainContract.View) : Presenter(context, view) {
    override fun refreshToken() {}
}