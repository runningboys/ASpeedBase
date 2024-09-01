package com.util.base.ui.main

import com.common.base.mvp.MvpBindingActivity
import com.util.base.databinding.ActivityMainBinding


class MainActivity : MvpBindingActivity<MainContract.Presenter, ActivityMainBinding>(), MainContract.View {

    override fun createPresenter(): MainContract.Presenter {
        return MainPresenter(this, this)
    }

    override fun initView() {
        binding.titleTv.text = "MainActivity"
    }

    override fun initListener() {

    }

    override fun initData() {
        mPresenter.queryContacts()
    }

    override fun onContacts() {

    }


}