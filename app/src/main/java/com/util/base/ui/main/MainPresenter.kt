package com.util.base.ui.main

import android.content.Context
import com.data.repository.UserRepository

class MainPresenter(context: Context, view: MainContract.View) : MainContract.Presenter(context, view) {
    override fun login(phone: String, password: String) = launch {
        UserRepository.login(phone, password).collect {
            mView?.onLogin()
        }
    }

}