package com.util.base.ui.two

import android.content.Context
import com.data.repository.UserRepository

class TwoPresenter(context: Context, view: TwoContract.View) : TwoContract.Presenter(context, view) {
    override fun login(phone: String, password: String) = launch {
        UserRepository.login(phone, password).collect {
            mView?.onLogin()
        }
    }

}