package com.util.base.ui.main

import com.common.base.mvp.MvpBindingActivity
import com.common.utils.ui.ToastUtil
import com.util.base.databinding.ActivityMainBinding


class MainActivity : MvpBindingActivity<MainContract.Presenter, ActivityMainBinding>(), MainContract.View {

    override fun createPresenter(): MainContract.Presenter {
        return MainPresenter(this, this)
    }

    override fun initView() {
        binding.titleTv.text = "登录"
    }

    override fun initListener() {
        binding.titleTv.setOnClickListener {
            mPresenter.login("13000000000", "12345")
        }
    }

    override fun initData() {

    }

    override fun onLogin() {
        ToastUtil.showToast("登录成功")
    }


}