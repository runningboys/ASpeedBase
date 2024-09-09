package com.util.base.ui.one

import androidx.activity.viewModels
import com.common.base.mvvm.MvvmBindingActivity
import com.common.utils.ui.ToastUtil
import com.data.network.manager.UIObserver
import com.data.network.model.UserBean
import com.util.base.AppManager
import com.util.base.databinding.ActivityOneBinding

class OneActivity : MvvmBindingActivity<ActivityOneBinding, OneViewModel>() {
    override val viewModel: OneViewModel by viewModels()


    override fun initView() {
    }

    override fun initListener() {
        binding.btnLogin.setOnClickListener { toLogin() }
    }

    override fun initData() {
    }

    private fun toLogin() {
        viewModel.login("xx", "xx").observe(this, object : UIObserver<UserBean>(this) {
            override fun onSucceed(result: UserBean) {
                AppManager.loginInit()
                ToastUtil.showToast("登录成功")
            }
        })
    }
}