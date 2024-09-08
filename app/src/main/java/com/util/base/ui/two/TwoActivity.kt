package com.util.base.ui.two

import com.common.base.mvp.MvpBindingActivity
import com.common.utils.ui.ToastUtil
import com.util.base.databinding.ActivityTwoBinding

class TwoActivity : MvpBindingActivity<ActivityTwoBinding, TwoContract.Presenter>(), TwoContract.View {

    override fun createPresenter(): TwoContract.Presenter {
        return TwoPresenter(this, this)
    }

    override fun initView() {
    }

    override fun initListener() {
        binding.btnLogin.setOnClickListener {
            mPresenter.login("xx", "x")
        }
    }

    override fun initData() {
    }

    override fun onLogin() {
        ToastUtil.showToast("登录成功")
    }
}