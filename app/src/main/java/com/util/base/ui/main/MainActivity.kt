package com.util.base.ui.main

import com.common.base.mvp.MvpActivity
import com.util.base.R
import com.util.base.ui.main.MainContract.Presenter

class MainActivity : MvpActivity<Presenter>(), MainContract.View {

    override fun getContentViewId(): Int {
        return R.layout.activity_main
    }

    override fun createPresenter(): Presenter {
        return MainPresenter(this, this)
    }

    override fun initData() {
        mPresenter?.refreshToken()
    }


    override fun showTips(message: String) {}


}