package com.util.base.ui.other

import androidx.activity.viewModels
import com.common.base.mvvm.MvvmBindingActivity
import com.common.utils.ui.ToastUtil
import com.data.network.manager.UIObserver
import com.data.network.model.UserBean
import com.util.base.databinding.ActivityOtherBinding

class OtherActivity : MvvmBindingActivity<OtherViewModel, ActivityOtherBinding>() {
    override val viewModel: OtherViewModel by viewModels()

    override fun initView() {
        binding.titleTv.text = "登录"
    }

    override fun initListener() {
        binding.titleTv.setOnClickListener { login() }
    }

    override fun initData() {

    }


    private fun login() {
        viewModel.login("13000000000", "12345").observe(this, object : UIObserver<UserBean>(this) {
            override fun onSucceed(result: UserBean) {
                ToastUtil.showToast("登录成功")
            }
        })
    }
}